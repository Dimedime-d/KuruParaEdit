package kplevelviewer.util;

//Decompression routine

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LempelZiv
{
    public static int[] decompress(int offset)
    {
        //Sometimes, invalid offset is read
        if (offset == 0 | offset > 0x7FFFFF) //Outside range of rom
        {
            return null;
        }
        try
        {
            //offset += 5; //Where size is located
            //Update: size IS located at offset!
            ROM.reader.seek(offset);
            byte[] fullSizeBytes = new byte[4];
            ROM.reader.readFully(fullSizeBytes);
            int totalBytes = (fullSizeBytes[0] & 0xFF) + ((fullSizeBytes[1] << 0x08) & 0xFF00)
                +((fullSizeBytes[2] << 0x10) & 0xFF0000) + ((fullSizeBytes[3] << 0x18) & 0xFF000000);
            //Next block to read map data!
            int bytesLeft = totalBytes; //2 bytes per tile
            int[] mapRAMBytes = new int[totalBytes];
            //1st 4 bytes are size because they are referenced when copying data
            /*for (int i = 0; i < sizeBytes.length; i++)
            {
                    mapRAMBytes[i] = sizeBytes[i];
            }*/
            ROM.reader.seek(offset + 4);

            //Decompression loop
            int currentPointer = 0, copyLimit = 0, copyPos = 0;

            while (bytesLeft > 0)
            {
                byte nextByte = ROM.reader.readByte();
                //Scrolls to next byte automatically

                if (nextByte < 0)
                    { //Copying back
                    
                    int length = ((nextByte & 0x7F) + 3);
                    bytesLeft -= length;

                    copyLimit = currentPointer + length;

                    int bytesBack = (ROM.reader.readByte() & 0xFF);
                    copyPos = currentPointer - bytesBack;
                    //System.out.println(Long.toHexString(ROM.reader.getFilePointer()) + " " + Integer.toHexString(nextByte) + " " + Integer.toHexString(bytesBack));
                    while (currentPointer < copyLimit) {  //Copy loop
                        //Temp fix
                        if (currentPointer >= mapRAMBytes.length) break; //Level 29 has terminating problems
                        mapRAMBytes[currentPointer++] = mapRAMBytes[copyPos++];
                    }
                } else
                    { //New Data
                    int length = (int) nextByte + 1; //0x7F rolls over to 0...
                    bytesLeft -= length;
                    //System.out.println("NEW " + Long.toHexString(ROM.reader.getFilePointer()) + " " + Integer.toHexString(length));
                    copyLimit = currentPointer + length;

                    while (currentPointer < copyLimit) {
                    int newTileID = ROM.reader.readByte();
                    //if (currentPointer >= mapRAMBytes.length) break;
                    mapRAMBytes[currentPointer++] = newTileID; //Should really be a byte
                    }
                }
                //Poss. print during the loop?
            }
            return mapRAMBytes;
        } catch (IOException ex) {
            Logger.getLogger(LempelZiv.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static int[] decompress(int offset, int maxSize)
    {
        //System.out.println(Integer.toHexString(offset));
        //Sometimes, invalid offset is read
        if (offset == 0 | offset > 0x7FFFFF) //Outside range of rom
        {
            return new int[maxSize];
        }
        try
        {
            ROM.reader.seek(offset);
            int bytesLeft = maxSize;

            int[] mapRAMBytes = new int[bytesLeft];

            //Decompression loop
            int currentPointer = 0, copyLimit = 0, copyPos = 0;

            while (bytesLeft > 0)
            {
                //System.out.println(Long.toHexString(ROM.reader.getFilePointer()));
                int nextByte = ROM.reader.readByte();
                //System.out.println(Integer.toHexString(nextByte));
                //Scrolls to next byte automatically
                if (nextByte < 0)
                    { //Copying back
                    int length = ((nextByte & 0x7F) + 3); //this goddamned type must be an INT, not a byte
                    bytesLeft -= length;

                    copyLimit = currentPointer + length;

                    int bytesBack = (ROM.reader.readByte() & 0xFF);
                    copyPos = currentPointer - bytesBack;

                    while (currentPointer < copyLimit) {  //Copy loop
                        if (currentPointer >= mapRAMBytes.length) break;
                        mapRAMBytes[currentPointer++] = mapRAMBytes[copyPos++];
                    }
                } else
                    { //New Data
                    int length = (int) nextByte + 1;
                    bytesLeft -= length;

                    copyLimit = currentPointer + length;

                    while (currentPointer < copyLimit)
                    {
                    int newTileID = ROM.reader.readByte(); //Auto-advances to next byte
                    if (currentPointer >= mapRAMBytes.length) break;
                    mapRAMBytes[currentPointer++] = newTileID; //Should really be a byte
                    }
                }
                //Poss. print during the loop?
            }
            return mapRAMBytes;
        } catch (IOException ex) {
            System.out.println("Map Data DNE");
            return null;
        }
    }

    public static int[] decompressGivenBytesToRead(int offset, int bytesToRead)
    {
        //System.out.println(Integer.toHexString(offset));
        //Sometimes, invalid offset is read
        //if (offset == 0 | offset > 0x7FFFFF) //Outside range of rom
        //{
        //    return null;
        //}
        try
        {
            ROM.reader.seek(offset);

            int[] mapRAMBytes = new int[0x10000];

            //Decompression loop
            int currentPointer = 0, copyLimit = 0, copyPos = 0;

            while (ROM.reader.getFilePointer() < offset+bytesToRead)
            {
                byte nextByte = ROM.reader.readByte();
                //Scrolls to next byte automatically
                if (nextByte < 0)
                    { //Copying back
                    int length = ((nextByte & 0x7F) + 3);
                    //bytesLeft -= length;

                    copyLimit = currentPointer + length;

                    int bytesBack = (ROM.reader.readByte() & 0xFF);
                    copyPos = currentPointer - bytesBack;

                    while (currentPointer < copyLimit) {  //Copy loop
                        if (currentPointer >= mapRAMBytes.length) break;
                        mapRAMBytes[currentPointer++] = mapRAMBytes[copyPos++];
                    }
                } else
                    { //New Data
                    int length = (int) nextByte + 1;
                    //bytesLeft -= length;

                    copyLimit = currentPointer + length;

                    while (currentPointer < copyLimit)
                    {
                    int newTileID = ROM.reader.readByte(); //Auto-advances to next byte
                    if (currentPointer >= mapRAMBytes.length) break;
                    mapRAMBytes[currentPointer++] = newTileID; //Should really be a byte
                    }
                }
                //Poss. print during the loop?
            }

            //Extra step to truncate the mapRAMBytes array
            int[] result = new int[currentPointer];
            //copy array
            System.arraycopy(mapRAMBytes, 0, result, 0, result.length);

            return result;
        } catch (IOException ex) {
            System.out.println("Map Data DNE");
            return null;
        }
    }

    //Now, to decompress an array of ints :P
    public static int[] decompress(int[] comp)
    {
        ArrayList<Integer> compData = new ArrayList<>();
        //Adding data to compData
        for (int i : comp)
        {
            compData.add(i);
        }
        ArrayList<Integer> decompData = new ArrayList<>();

        Iterator reader = compData.iterator();

        int currentDecompPointer = 0, currentCompPointer = 0, copyLimit = 0, copyPos = 0;
        while (reader.hasNext())
        {   //Iterate through each byte of compressed data
            int nextByte = ((Integer) reader.next() & 0xFF);
            currentCompPointer++;
            if(nextByte > 0x7F) //Copying data
            {
                int copyLength = (nextByte & 0x7F) + 3;
                int bytesBack = ((Integer) reader.next() & 0xFF);
                currentCompPointer++;

                copyLimit = currentDecompPointer + copyLength;
                copyPos = currentDecompPointer - bytesBack;

                while (currentDecompPointer < copyLimit)
                {
                    if (copyPos >= decompData.size()) break;
                    decompData.add(decompData.get(copyPos)); //Copy a byte
                    copyPos++;
                    currentDecompPointer++;
                }
            } else { //Adding new data from compressed data
                int length = ++nextByte;

                copyLimit = currentCompPointer + length;
                while (currentCompPointer < copyLimit)
                {
                    if (currentCompPointer >= comp.length) break;
                    decompData.add((Integer) reader.next());
                    currentDecompPointer++;
                    currentCompPointer++;
                }

            }
        }

        return decompData.stream().mapToInt(i -> i).toArray();
    }
     //"Legacy" stuff
    //Attempt a compression algorithim
    public static int[] compress_old(int[] orig)
    {
        ArrayList<Integer> compList = new ArrayList<>();

        int literalCount = 0; //Counts # of "regular" (not copied) bytes read
        for (int i = 0; i < orig.length; i++)
        {
            //For each byte in the array...
            //Search for potential repeated bytes! (from 3 to 0x82 bytes back

            int bytesBack = 1;
            ArrayList<Integer> copyLengthNums = new ArrayList<>();
            ArrayList<Integer> bytesBackNums = new ArrayList<>();
            int currentPointer = i;

            while (bytesBack <= 0xFF)
                //Iterate through each byte back in each byte in array
            {
                //Search for byte repeats in here
                int searchIndex = i - bytesBack;
                if (searchIndex < 0) break;

                //Arrays to hold copyLength & bytesBack values
                //To take the max of them later


                //Search for matches
                if (orig[searchIndex] != orig[i])
                {
                    bytesBack++;
                } else
                { //You DO have a match
                    int copyLength = 0; //Represents # of bytes to copy
                    currentPointer = i;
                    do
                    {
                        copyLength++;

                        searchIndex++;
                        currentPointer++;

                    } while (currentPointer < orig.length &&
                            orig[searchIndex] == orig[currentPointer] &&
                            copyLength < 0x83);

                    if(copyLength > 2)
                    {
                        copyLengthNums.add(copyLength);
                        bytesBackNums.add(bytesBack);
                    }

                    bytesBack++;
                }
            }

            //If copyLengths > 2 has been found, need to insert bytes!
            if (copyLengthNums.size() > 0)
            {
                //Reset literal count and add literals to compList
                //(Before adding compressed info)
                if (literalCount > 0)
                {
                    int copyLength2 = literalCount;
                    compList.add(copyLength2 - 1);
                    literalCount = 0;
                    //System.out.printf("Literal count of length %x at position"
                    //        + " %x (uncompressed array position %x)", copyLength2, compList.size(), i);
                    //System.out.println("");
                    while (copyLength2 > 0)
                    {
                        compList.add((orig[i - copyLength2]) & 0xFF);
                        copyLength2--;
                    }
                }

                //TODO - avoid reliance on "index of"
                int maxCopyLength = Collections.max(copyLengthNums);
                int maxCopyLengthIndex = copyLengthNums.indexOf(maxCopyLength);
                int corrBytesBack = bytesBackNums.get(maxCopyLengthIndex);

                /*System.out.printf("Copy length %x at %x bytes back at "
                                + "position %x", maxCopyLength, corrBytesBack, compList.size());
                        System.out.println("");*/

                int byte1 = ((maxCopyLength - 3) + 0x80) & 0xFF;
                int byte2 = corrBytesBack & 0xFF;

                compList.add(byte1);
                compList.add(byte2);

                copyLengthNums.clear();
                bytesBackNums.clear();

                //Skip copyLength number of bytes in original uncompressed array
                i += maxCopyLength - 1;
            } else { //No places to copy from - following bytes are literals
                //fix me
                literalCount++;
                if (literalCount > 0x80) //>=0x7F Before
                {
                    int copyLength2 = literalCount;
                    compList.add(copyLength2 - 1);
                    literalCount = 0;
                    System.out.printf("Literal count of length %x at position"
                            + " %x (uncompressed array position %x)", copyLength2, compList.size(), i);
                    System.out.println("");
                    while (copyLength2 > 0)
                    {
                        compList.add((orig[i - copyLength2]) & 0xFF);
                        copyLength2--;
                    }
                }
            }

        }

        //Add last literals
        if (literalCount > 0)
        {
            int copyLength2 = literalCount;
            compList.add(copyLength2 - 1);
            literalCount = 0;
            while (copyLength2 > 0)
            {
                compList.add((orig[orig.length - copyLength2]) & 0xFF);
                copyLength2--;
            }
        }
        return compList.stream().mapToInt(i -> i).toArray();
        //Casts arraylist to array
    }
    
    
    public static final int PREFIX_MIN_LENGTH = 3;
    
    public static byte[] compress(int[] orig)
    {
        ArrayList<Byte> compList = new ArrayList<>();
        
        int cursor = 0;
        while (cursor < orig.length)
        {
            int offset = findLongestPrefixOffset(orig, cursor); //find the position where copying would give most # of bytes
            int len = prefixLength(orig, cursor + offset, cursor); //make prefix length going forward?
            if (len < PREFIX_MIN_LENGTH) //not enough data to copy - write raw data
            {
                len = 1;
                while (len < 0x80 && len + cursor < orig.length &&
                    prefixLength(orig, cursor + len + findLongestPrefixOffset(orig, cursor + len), cursor + len) < PREFIX_MIN_LENGTH) //uhh what?
                {
                    len++;
                }    

                compList.add((byte)(len-1));
                for (; len > 0; len--)
                {
                    compList.add((byte)orig[cursor]);
                    cursor++;
                }
            } else {
                compList.add((byte)(0x80 + len - PREFIX_MIN_LENGTH));
                compList.add((byte)-offset);
                cursor += len;
            }
        }
        Object[] arr = compList.toArray();
        byte[] data = new byte[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            data[i] = (byte) arr[i];
        }
        return data;
    }
    
    private static int findLongestPrefixOffset(int[] data, int cursor)
    {
        int maxL = 0;
        int res = 0;
        int leftCursor = cursor - 1; //go backwards in the data iterated up to this point
        while (leftCursor >= 0 && cursor - leftCursor < 0xFF) //limit # of bytes to reference back
        {
            int l = prefixLength(data, leftCursor, cursor);
            if (l > maxL)
            {
                maxL = l;
                res = leftCursor - cursor; //a negative number
            }
            leftCursor--;
        }
        return res;
    }
    
    private static int prefixLength(int[] data, int leftCursor, int rightCursor)
    {
        if (leftCursor >= rightCursor)
            return 0;
        int i = 0;
        while (rightCursor < data.length && i < 0x7F + PREFIX_MIN_LENGTH) //2 constraints: end of data + limit of bytes to copy from
        {
            if (data[leftCursor] != data[rightCursor]) //look for data to copy
                break;
            i++;
            leftCursor++;
            rightCursor++;
        }
        return i;
    }
}
