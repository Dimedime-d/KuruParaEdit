package kplevelviewer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import kplevelviewer.util.LempelZiv;
import kplevelviewer.util.ROM;

public class OffsetGroup {
    
    public interface DecompActor
    {
        public int getChineseLB();
        public Palette createUncompPalette() throws IOException;
        public ImageSet createImageSet() throws IOException;
        public BufferedImage[][] createMapImages() throws IOException;
        public DrawPanel createDrawPanel(BufferedImage im) throws IOException;
        public default String getFilePath() {
            return "out/" + this.toString() + ".png";
        };
    }
    
    public static void swapROM(int index, DecompActor a)
    {
        int chineseLB = a.getChineseLB();
        if (index >= chineseLB && ROM.lang != ROM.CHN)
        {
            ROM.changeRomAccessToChinese();
        } else if (index < chineseLB && ROM.lang != ROM.JPN)
        {
            ROM.changeRomAccessToJapanese();
        }
    }
    
    public static void writePalette(int[] paletteData, int palettePtr, int posInPalette, int paletteSize) throws IOException
    {
        //Remember, changing arrays here will affect it outside the method...
        ROM.reader.seek(palettePtr);
        byte[] paletteBytes = new byte[paletteSize];
        ROM.reader.readFully(paletteBytes);
        for (int i = posInPalette; i < posInPalette + paletteSize; i++)
        {
            paletteData[i] = paletteBytes[i - posInPalette];
        }
    }
    
    public enum AdventureMap
    {
        T1 (0x381B34, 0x382ABC, 0x0, 0x383694, 0x3849B0, 0x384B0C, 0x384FEC, 0x0, 0x384668, 0x3847B0),
        T2 (0x381B34, 0x382ABC, 0x0, 0x383694, 0x386240, 0x3863C8, 0x384FEC, 0x0, 0x384668, 0x3847B0),
        T3 (0x381B34, 0x382ABC, 0x0, 0x383694, 0x386BCC, 0x386EB8, 0x384FEC, 0x0, 0x384668, 0x3847B0),
        T4 (0x381B34, 0x382ABC, 0x0, 0x383694, 0x387818, 0x3879E4, 0x384FEC, 0x0, 0x384668, 0x3847B0),
        T5 (0x381B34, 0x382ABC, 0x0, 0x383694, 0x388254, 0x388408, 0x384FEC, 0x0, 0x384668, 0x3847B0),
        W11 (0x388BC4, 0x38967C, 0x38B50C, 0x38CC34, 0x39C460, 0x39C7A0, 0x39EF24, 0x39F594, 0x38DC08, 0x38DF00),
        W12 (0x388BC4, 0x38967C, 0x38B50C, 0x38CC34, 0x39FF58, 0x3A0568, 0x3A1E04, 0x39F594, 0x38DC08, 0x38DF00),
        W13 (0x388BC4, 0x38967C, 0x38B50C, 0x38CC34, 0x3A269C, 0x3A2F6C, 0x3A5B20, 0x39F594, 0x38DC08, 0x38DF00),
        W14 (0x38E100, 0x38F324, 0x392190, 0x38CC34, 0x3A68B0, 0x3A7C0C, 0x3A96C8, 0x3AF930, 0x3953D0, 0x395684),
        W15 (0x38E100, 0x38F324, 0x392190, 0x38CC34, 0x3B09D8, 0x3B29BC, 0x3B47C8, 0x3BBCA0, 0x3953D0, 0x395684),
        W15A (0x395884, 0x397070, 0x399564, 0x38CC34, 0x3BD860, 0x3BE288, 0x3BF024, 0x3C4228, 0x39BF4C, 0x39C260),
        W14A (0x395884, 0x397070, 0x399564, 0x38CC34, 0x3C51F8, 0x3BE288, 0x3C5ED8, 0x3C9730, 0x39BF4C, 0x39C260),
        W14B (0x395884, 0x397070, 0x399564, 0x38CC34, 0x3CABB8, 0x3BE288, 0x3CBB44, 0x3CF970, 0x39BF4C, 0x39C260),
        W21 (0x3EC8DC, 0x3EF6E8, 0x0, 0x3F1E3C, 0x3FD31C, 0x3FDFE8, 0x3FEAB0, 0x3FF37C, 0x3F2E18, 0x3F30F4),
        W22 (0x3EC8DC, 0x3EF6E8, 0x0, 0x3F1E3C, 0x400C94, 0x401F2C, 0x402CD0, 0x3FF37C, 0x3F2E18, 0x3F30F4),
        W23 (0x3EB074, 0x0, 0x3EF6E8, 0x3F1E3C, 0x403694, 0x40532C, 0x4068AC, 0xFFFFFF, 0x3F2E18, 0x3F30F4),
        W24 (0x3F32F4, 0x3F46C0, 0x3F6890, 0x3F1E3C, 0x407658, 0x40A05C, 0x40C058, 0x40E734, 0x3F84C8, 0x3F87D4),
        W25 (0x3F32F4, 0x3F46C0, 0x3F6890, 0x3F1E3C, 0x40F70C, 0x411AEC, 0x415630, 0x417CA8, 0x3F84C8, 0x3F87D4),
        W25A (0x3F32F4, 0x3F46C0, 0x3F6890, 0x3F1E3C, 0x41BB38, 0x41D9A0, 0x41F35C, 0x421500, 0x3F84C8, 0x3F87D4),
        W23A (0x3F89D4, 0x3F9C64, 0x3FBD18, 0x3F1E3C, 0x425C28, 0x427A28, 0x42A1F4, 0x42E1B4, 0x3FCDB4, 0x3FD11C),
        W23B (0x3F89D4, 0x3F9C64, 0x3FBD18, 0x3F1E3C, 0x42E978, 0x4303D0, 0x432D94, 0x43817C, 0x3FCEDC, 0x3FD11C),
        W31 (0x44D50C, 0x44E1A0, 0x4500E4, 0x4527A4, 0x460FE0, 0x461BA4, 0x462B38, 0x4668D0, 0x453778, 0x453A58),
        W32 (0x44D50C, 0x44E1A0, 0x4500E4, 0x4527A4, 0x46CB94, 0x46E39C, 0x462B38, 0x4668D0, 0x453778, 0x453A58),
        W33 (0x44D50C, 0x44E1A0, 0x4500E4, 0x4527A4, 0x46FB58, 0x471D40, 0x462B38, 0x4668D0, 0x453778, 0x453A58),
        W34 (0x453C58, 0x454EDC, 0x456A08, 0x4527A4, 0x4742EC, 0x475A10, 0x478A0C, 0x47A8F8, 0x458A04, 0x458CE4),
        W35 (0x453C58, 0x454EDC, 0x456A08, 0x4579B4, 0x47CCD4, 0x47E744, 0x4814C4, 0x4840E8, 0x458A04, 0x458CE4),
        W34A (0x453C58, 0x454EDC, 0x456A08, 0x4527A4, 0x4865C8, 0x487F94, 0x48AEB4, 0x48D140, 0x458A04, 0x458CE4),
        W33A (0x458EE4, 0x45A868, 0x45DAE8, 0x4527A4, 0x48F588, 0x490FC8, 0x492E24, 0x494124, 0x460AF0, 0x460DE0),
        W33B (0x458EE4, 0x45A868, 0x45DAE8, 0x4527A4, 0x4960EC, 0x497DD8, 0x49A674, 0x49BACC, 0x460AF0, 0x460DE0),
        W41 (0x4C1470, 0x4C24D8, 0x4C4B44, 0x4C69C0, 0x4D38A0, 0x4D50B4, 0x4D5468, 0x4DBD3C, 0x4C79FC, 0x4C7C6C),
        W42 (0x4C1470, 0x4C24D8, 0x4C4B44, 0x4C69C0, 0x4DC600, 0x4DF6FC, 0x4DFBF0, 0x4E4AC4, 0x4C79FC, 0x4C7C6C),
        W43 (0x4C1470, 0x4C24D8, 0x4C4B44, 0x4C69C0, 0x4E5488, 0x4E9B84, 0x4EA254, 0x4F2178, 0x4C79FC, 0x4C7C6C),
        W44 (0x4C7E6C, 0x4C98B0, 0x4CCB74, 0x4C69C0, 0x4F2B00, 0x0, 0x4F49A0, 0x4F7994, 0x4CE764, 0x4CE9EC),
        W45 (0x4CEBEC, 0x4D0480, 0x4D1B20, 0x4C69C0, 0x4F9788, 0x0, 0x4FA2C0, 0x0, 0x4D3360, 0x4D36A0),
        W46 (0x4CEBEC, 0x4D0480, 0x4D1B20, 0x4C69C0, 0x4FCF10, 0x4F9F30, 0x4FE08C, 0x501238, 0x4D3360, 0x4D36A0),
        W44A (0x4C7E6C, 0x4C98B0, 0x4CCB74, 0x4C69C0, 0x503C1C, 0x505C68, 0x50BE80, 0x510F90, 0x4CE764, 0x4CE9EC),
        W44B (0x4C7E6C, 0x4C98B0, 0x4CCB74, 0x4C69C0, 0x51370C, 0x5158E4, 0x5190A4, 0x51CEE0, 0x4CE764, 0x4CE9EC),
        W44C (0x4CEBEC, 0x4D0480, 0x4D1B20, 0x4C69C0, 0x51F0E0, 0x4F9F30, 0x521D90, 0x52486C, 0x4D3360, 0x4D36A0),
        SC1 (0x5382A0, 0x538DCC, 0x53A96C, 0x53BFC4, 0x53D378, 0x53ECA8, 0x540F18, 0x541F08, 0x53D000, 0x53D178),
        SC2 (0x5382A0, 0x538DCC, 0x53A96C, 0x53BFC4, 0x5429A0, 0x5448F4, 0x546B4C, 0x541F08, 0x53D000, 0x53D178),
        SC3 (0x5382A0, 0x538DCC, 0x53A96C, 0x53BFC4, 0x547CF4, 0x54A220, 0x54C574, 0x541F08, 0x53D000, 0x53D178),
        SC4 (0x5382A0, 0x538DCC, 0x53A96C, 0x53BFC4, 0x54D1A0, 0x54EE60, 0x540F18, 0x541F08, 0x53D000, 0x53D178),
        //broken, need to fix tilemap #3 (dive into source code)... also, the start/endzones are embedded as objects that affect the bg map
        CH11 (0x0, 0x0, 0x55463C, 0x554DA4, 0x555C14, 0x555E10, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH12 (0x0, 0x0, 0x55463C, 0x554DA4, 0x55604C, 0x556400, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH13 (0x0, 0x0, 0x55463C, 0x554DA4, 0x556650, 0x556EDC, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH21 (0x0, 0x0, 0x55463C, 0x554DA4, 0x5571E4, 0x557F18, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH22 (0x0, 0x0, 0x55463C, 0x554DA4, 0x558380, 0x5594C8, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH23 (0x0, 0x0, 0x55463C, 0x554DA4, 0x5598D4, 0x55A408, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH31 (0x0, 0x0, 0x55463C, 0x554DA4, 0x55A858, 0x55AC74, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH32 (0x0, 0x0, 0x55463C, 0x554DA4, 0x55AECC, 0x55BF3C, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH33 (0x0, 0x0, 0x55463C, 0x554DA4, 0x55C240, 0x55D0BC, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH41 (0x0, 0x0, 0x55463C, 0x554DA4, 0x55D434, 0x55D78C, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH42 (0x0, 0x0, 0x55463C, 0x554DA4, 0x55D964, 0x55DC4C, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH43 (0x0, 0x0, 0x55463C, 0x554DA4, 0x55DEA8, 0x55E2DC, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH51 (0x0, 0x0, 0x55463C, 0x554DA4, 0x55E57C, 0x55E87C, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH52 (0x0, 0x0, 0x55463C, 0x554DA4, 0x55EA3C, 0x55F174, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH53 (0x0, 0x0, 0x55463C, 0x554DA4, 0x55F41C, 0x55F79C, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH61 (0x0, 0x0, 0x55463C, 0x554DA4, 0x55FAB4, 0x55FFC8, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH62 (0x0, 0x0, 0x55463C, 0x554DA4, 0x5602CC, 0x560748, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH63 (0x0, 0x0, 0x55463C, 0x554DA4, 0x5609F0, 0x560F14, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH71 (0x0, 0x0, 0x55463C, 0x554DA4, 0x5611A8, 0x5614E0, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH72 (0x0, 0x0, 0x55463C, 0x554DA4, 0x561720, 0x5620A0, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH73 (0x0, 0x0, 0x55463C, 0x554DA4, 0x5623F4, 0x5629A8, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH81 (0x0, 0x0, 0x55463C, 0x554DA4, 0x562CCC, 0x56382C, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH82 (0x0, 0x0, 0x55463C, 0x554DA4, 0x563B60, 0x564160, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH83 (0x0, 0x0, 0x55463C, 0x554DA4, 0x564450, 0x564DA4, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH91 (0x0, 0x0, 0x55463C, 0x554DA4, 0x5650DC, 0x5653EC, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH92 (0x0, 0x0, 0x55463C, 0x554DA4, 0x5655A4, 0x565A20, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH93 (0x0, 0x0, 0x55463C, 0x554DA4, 0x565CC4, 0x565FC8, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH101 (0x0, 0x0, 0x55463C, 0x554DA4, 0x5661AC, 0x566B98, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH102 (0x0, 0x0, 0x55463C, 0x554DA4, 0x566F90, 0x567264, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH103 (0x0, 0x0, 0x55463C, 0x554DA4, 0x567448, 0x567C50, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH111 (0x0, 0x0, 0x55463C, 0x554DA4, 0x56813C, 0x5684C4, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH112 (0x0, 0x0, 0x55463C, 0x554DA4, 0x568718, 0x568A98, 0x555EBC, 0x0, 0x555914, 0x555A14),
        CH113 (0x0, 0x0, 0x55463C, 0x554DA4, 0x568DEC, 0x569020, 0x555EBC, 0x0, 0x555914, 0x555A14);
        
        private final int[] tilesetOffsets = new int[4];
        private final int[] tileMapOffsets = new int[4];
        private final int compressedPaletteOffset;
        private final int wallPaletteOffset;

        AdventureMap(int tileOffsets0, int tileOffsets1, int tileOffsets2, int tileOffsets3,
                int tileMapOffset0, int tileMapOffset1, int tileMapOffset2, int tileMapOffset3,
                int compPalOffset, int wallPalOffset)
        {
                tilesetOffsets[0] = tileOffsets0;
                tilesetOffsets[1] = tileOffsets1;
                tilesetOffsets[2] = tileOffsets2;
                tilesetOffsets[3] = tileOffsets3;

                tileMapOffsets[0] = tileMapOffset0;
                tileMapOffsets[1] = tileMapOffset1;
                tileMapOffsets[2] = tileMapOffset2;
                tileMapOffsets[3] = tileMapOffset3;

                compressedPaletteOffset = compPalOffset;
                wallPaletteOffset = wallPalOffset;
        }

        public int[] getTilesetOffsets() {
            return tilesetOffsets;
        }

        public int[] getTileMapOffsets() {
            return tileMapOffsets;
        }

        public int getCompressedPaletteOffset() {
            return compressedPaletteOffset;
        }

        public int getWallPaletteOffset() {
            return wallPaletteOffset;
        }
    }
	
    public enum MinigameSplash implements DecompActor
    {
        //2E6F4
        //MINI0 (0x0, 0x2339F4, 0x0, 0x0),
        //MINI1 (0x0, 0x2339F4, 0x0, 0x0),
        MINI2 (0x2339F4, 0x233BF4, 0x1094, 0x234C88),
        MINI3 (0x234F58, 0x235158, 0x15EB, 0x236744),
        MINI4 (0x244EEC, 0x2450EC, 0x1315, 0x246404),
        //MINI5 (0x0, 0x236A68, 0x0, 0x0),
        MINI6 (0x236A68, 0x236C68, 0x19A1, 0x23860C),
        MINI7 (0x23898C, 0x238B8C, 0x146E, 0x239FFC),
        MINI8 (0x23A2C4, 0x23A4C4, 0x1334, 0x23B7F8),
        MINI9 (0x23BAFC, 0x23BCFC, 0x12E0, 0x23CFDC),
        // MINI10 (0x0, 0x23D2C4, 0x0, 0x0),
       // MINI11 (0x0, 0x23D2C4, 0x0, 0x0),
        MINI12 (0x23D2C4, 0x23D4C4, 0x18D9, 0x23EDA0),
        MINI13 (0x23F0DC, 0x23F2DC, 0x13AD, 0x24068C),
        MINI14 (0x2409A0, 0x240BA0, 0xFC0, 0x241B60),
        MINI15 (0x241E0C, 0x24200C, 0x13EB, 0x2433F8),
        MINI16 (0x243744, 0x243944, 0x12A6, 0x244BEC),
        //MINI17 (0x0, 0x246730, 0x0, 0x0),
        MINI18 (0x246730, 0x246930, 0x111D, 0x247A50),
        MINI19 (0x247CE4, 0x247EE4, 0x113D, 0x249024),
        MINI20 (0x24930C, 0x24950C, 0x1606, 0x24AB14),
        MINI21 (0x25D3F8, 0x25D5F8, 0x1434, 0x25EA2C),
        MINI22 (0x24AE38, 0x24B038, 0x1031, 0x24C06C),
        MINI23 (0x24C33C, 0x24C53C, 0x192B, 0x24DE68),
        MINI24 (0x24E1D8, 0x24E3D8, 0x13CA, 0x24F7A4),
        MINI25 (0x24FA5C, 0x24FC5C, 0xFDD, 0x250C3C),
        MINI26 (0x250EDC, 0x2510DC, 0x12E0, 0x2523BC),
        MINI27 (0x2526A4, 0x2528A4, 0x1480, 0x253D24),
        MINI28 (0x254024, 0x254224, 0x18F6, 0x255B1C),
        MINI29 (0x255E74, 0x256074, 0x15EB, 0x257660),
        MINI30 (0x25795C, 0x257B5C, 0x1537, 0x259094),
        MINI31 (0x2593E8, 0x2595E8, 0xDDA, 0x25A3C4),
        MINI32 (0x25A628, 0x25A828, 0x1298, 0x25BAC0),
        MINI33 (0x25BDE8, 0x25BFE8, 0x114A, 0x25D134),
        //MINI34 (0x0, 0x25ED80, 0x0, 0x0),
        MINI35 (0x25ED80, 0x25EF80, 0x13C8, 0x260348),
        MINI36 (0x260618, 0x260818, 0x1290, 0x261AA8),
        MINI37 (0x261DCC, 0x261FCC, 0x1807, 0x2637D4),
        MINI38 (0x2777F4, 0x2779F4, 0x13A3, 0x278D98),
        MINI39 (0x263B24, 0x263D24, 0x135B, 0x265080),
        MINI40 (0x265398, 0x265598, 0x18F4, 0x266E8C),
        MINI41 (0x267200, 0x267400, 0x171E, 0x268B20),
        MINI42 (0x268E24, 0x269024, 0x159F, 0x26A5C4),
        MINI43 (0x26A8F0, 0x26AAF0, 0x105C, 0x26BB4C),
        MINI44 (0x26BDDC, 0x26BFDC, 0x1621, 0x26D600),
        MINI45 (0x26D928, 0x26DB28, 0x1A3D, 0x26F568),
        MINI46 (0x26F8C4, 0x26FAC4, 0x18AF, 0x271374),
        MINI47 (0x2716B0, 0x2718B0, 0x14EB, 0x272D9C),
        MINI48 (0x2730DC, 0x2732DC, 0xD4C, 0x274028),
        MINI49 (0x274284, 0x274484, 0x17DF, 0x275C64),
        MINI50 (0x276000, 0x276200, 0x12EF, 0x2774F0), //44 of these Japanese Minigame splashes
        
        //MINI0_CHN (0x0, 0x2332E8, 0x0, 0x0),
        //MINI1_CHN (0x0, 0x2332E8, 0x0, 0x0),
        MINI2_CHN (0x2332E8, 0x2334E8, 0xF02, 0x2343EC),
        MINI3_CHN (0x234624, 0x234824, 0x13D4, 0x235BF8),
        MINI4_CHN (0x2438E0, 0x243AE0, 0x13F3, 0x244ED4),
        //MINI5_CHN (0x0, 0x235E78, 0x0, 0x0),
        MINI6_CHN (0x235E78, 0x236078, 0x185D, 0x2378D8),
        MINI7_CHN (0x237BD0, 0x237DD0, 0x1335, 0x239108),
        MINI8_CHN (0x239330, 0x239530, 0x1237, 0x23A768),
        MINI9_CHN (0x23A9EC, 0x23ABEC, 0x1245, 0x23BE34),
        //MINI10_CHN (0x0, 0x23C0D0, 0x0, 0x0),
        //MINI11_CHN (0x0, 0x23C0D0, 0x0, 0x0),
        MINI12_CHN (0x23C0D0, 0x23C2D0, 0x1717, 0x23D9E8),
        MINI13_CHN (0x23DCAC, 0x23DEAC, 0x1344, 0x23F1F0),
        MINI14_CHN (0x23F48C, 0x23F68C, 0xF2A, 0x2405B8),
        MINI15_CHN (0x2407DC, 0x2409DC, 0x12C5, 0x241CA4),
        MINI16_CHN (0x241F64, 0x242164, 0x14C1, 0x243628),
        //MINI17_CHN (0x0, 0x2451B8, 0x0, 0x0),
        MINI18_CHN (0x2451B8, 0x2453B8, 0x105C, 0x246414),
        MINI19_CHN (0x24663C, 0x24683C, 0xF6C, 0x2477A8),
        MINI20_CHN (0x2479DC, 0x247BDC, 0x13D4, 0x248FB0),
        MINI21_CHN (0x25A83C, 0x25AA3C, 0x14EA, 0x25BF28),
        MINI22_CHN (0x249228, 0x249428, 0xFFA, 0x24A424),
        MINI23_CHN (0x24A684, 0x24A884, 0x1773, 0x24BFF8),
        MINI24_CHN (0x24C2D8, 0x24C4D8, 0x1379, 0x24D854),
        MINI25_CHN (0x24DA90, 0x24DC90, 0xEEE, 0x24EB80),
        MINI26_CHN (0x24EDA8, 0x24EFA8, 0x123B, 0x2501E4),
        MINI27_CHN (0x250494, 0x250694, 0x1434, 0x251AC8),
        MINI28_CHN (0x251D5C, 0x251F5C, 0x16DC, 0x253638),
        MINI29_CHN (0x2538D0, 0x253AD0, 0x141F, 0x254EF0),
        MINI30_CHN (0x2551A4, 0x2553A4, 0x14B1, 0x256858),
        MINI31_CHN (0x256B04, 0x256D04, 0xC8E, 0x257994),
        MINI32_CHN (0x257B80, 0x257D80, 0x114F, 0x258ED0),
        MINI33_CHN (0x259158, 0x259358, 0x125D, 0x25A5B8),
        //MINI34_CHN (0x0, 0x25C21C, 0x0, 0x0),
        MINI35_CHN (0x25C21C, 0x25C41C, 0x12A5, 0x25D6C4),
        MINI36_CHN (0x25D924, 0x25DB24, 0x110A, 0x25EC30),
        MINI37_CHN (0x25EE94, 0x25F094, 0x154A, 0x2605E0),
        MINI38_CHN (0x273640, 0x273840, 0x1439, 0x274C7C),
        MINI39_CHN (0x260880, 0x260A80, 0x1309, 0x261D8C),
        MINI40_CHN (0x262028, 0x262228, 0x16F0, 0x263918),
        MINI41_CHN (0x263BF0, 0x263DF0, 0x1587, 0x265378),
        MINI42_CHN (0x2655E8, 0x2657E8, 0x1414, 0x266BFC),
        MINI43_CHN (0x266EA0, 0x2670A0, 0x11B8, 0x268258),
        MINI44_CHN (0x268504, 0x268704, 0x1617, 0x269D1C),
        MINI45_CHN (0x269FF0, 0x26A1F0, 0x1833, 0x26BA24),
        MINI46_CHN (0x26BCC8, 0x26BEC8, 0x16EA, 0x26D5B4),
        MINI47_CHN (0x26D870, 0x26DA70, 0x14B1, 0x26EF24),
        MINI48_CHN (0x26F1C4, 0x26F3C4, 0xC68, 0x27002C),
        MINI49_CHN (0x270214, 0x270414, 0x15F5, 0x271A0C),
        MINI50_CHN (0x271D28, 0x271F28, 0x1469, 0x273394);
        
        private final int paletteOffset;
        private final int tilesetOffset;
        private final int tilesetSize;
        private final int tileMapOffset;

        MinigameSplash(int palettePtr, int tilesetPtr, int tilesetLength, int tileMapPtr)
        {
                paletteOffset = palettePtr;
                tilesetOffset = tilesetPtr;
                tilesetSize = tilesetLength;
                tileMapOffset = tileMapPtr;
        }
        
        @Override
        public Palette createUncompPalette() throws IOException
        {
            int[] paletteData = new int[0x200];
            writePalette(paletteData, paletteOffset, 0, 0x80);
            return new Palette(paletteData);
        }
        
        @Override
        public ImageSet createImageSet() throws IOException
        {
            Palette palette = createUncompPalette();
            int[] tileset = LempelZiv.decompress(tilesetOffset, tilesetSize*4);
            int[][] tilesetArgs = {new int[0x4000], tileset, new int[0x4000], new int[0x4000]};
            return new ImageSet(palette, tilesetArgs);
        }
        
        @Override
        public BufferedImage[][] createMapImages() throws IOException
        {
            ImageSet tilesetImages = createImageSet();
            
            int[] tileMap = LempelZiv.decompress(tileMapOffset, 0x4B0);
            //debug
            KPCompressionWiz.outputBinaryFile(tileMap);
            return tilesetImages.createTileMap(tileMap, 0x200, 30, 30);
        }

        @Override
        public int getChineseLB() 
        {
            return 44;
        }

        @Override
        public DrawPanel createDrawPanel(BufferedImage im) throws IOException 
        {
            return new DrawPanel(im.getWidth(), im.getHeight(), ImageIO.read(new File("res/minigameSplashBG.BMP")), im);
        }

        @Override
        public String getFilePath() 
        {
            return "out/" + this.toString() + ".png";
        }
    }
    
    public enum MagicPreview implements DecompActor
    {
        MAGIC_PREVIEW0 (0x1C6948, 0x1C7554, 0x1C7774),
        MAGIC_PREVIEW1 (0x1C7974, 0x1C83F0, 0x1C85A4),
        MAGIC_PREVIEW2 (0x1C87A4, 0x1C9114, 0x1C92DC),
        MAGIC_PREVIEW3 (0x1C94DC, 0x1C9D34, 0x1C9EEC),
        MAGIC_PREVIEW4 (0x1CA0EC, 0x1CAA4C, 0x1CAC08),
        MAGIC_PREVIEW5 (0x1CAE08, 0x1CB77C, 0x1CB928),
        MAGIC_PREVIEW6 (0x1CBB28, 0x1CC360, 0x1CC4A4),
        MAGIC_PREVIEW7 (0x1CC6A4, 0x1CD158, 0x1CD34C),
        MAGIC_PREVIEW8 (0x1CD54C, 0x1CDDB8, 0x1CDF10),
        MAGIC_PREVIEW9 (0x1CE110, 0x1CE920, 0x1CEAB8),
        MAGIC_PREVIEW10 (0x1CECB8, 0x1CF730, 0x1CF8E8),
        MAGIC_PREVIEW11 (0x1CFAE8, 0x1D0440, 0x1D05D8),
        MAGIC_PREVIEW12 (0x1D07D8, 0x1D1238, 0x1D141C),
        MAGIC_PREVIEW13 (0x1D161C, 0x1D1E78, 0x1D1FEC),
        MAGIC_PREVIEW14 (0x1D21EC, 0x1D2CA0, 0x1D2E90),
        MAGIC_PREVIEW15 (0x1D3090, 0x1D3964, 0x1D3B08),
        
        MAGIC_PREVIEW0_CHN (0x1C5F10, 0x1C6B94, 0x1C6DDC),
        MAGIC_PREVIEW1_CHN (0x1C6FDC, 0x1C7A7C, 0x1C7C18),
        MAGIC_PREVIEW2_CHN (0x1C7E18, 0x1C87E4, 0x1C8994),
        MAGIC_PREVIEW3_CHN (0x1C8B94, 0x1C93EC, 0x1C955C),
        MAGIC_PREVIEW4_CHN (0x1C975C, 0x1CA130, 0x1CA2EC),
        MAGIC_PREVIEW5_CHN (0x1CA4EC, 0x1CAF0C, 0x1CB0C4),
        MAGIC_PREVIEW6_CHN (0x1CB2C4, 0x1CBB74, 0x1CBCB0),
        MAGIC_PREVIEW7_CHN (0x1CBEB0, 0x1CCA44, 0x1CCC40),
        MAGIC_PREVIEW8_CHN (0x1CCE40, 0x1CD738, 0x1CD88C),
        MAGIC_PREVIEW9_CHN (0x1CDA8C, 0x1CE384, 0x1CE520),
        MAGIC_PREVIEW10_CHN (0x1CE720, 0x1CF104, 0x1CF298),
        MAGIC_PREVIEW11_CHN (0x1CF498, 0x1CFD0C, 0x1CFE40),
        MAGIC_PREVIEW12_CHN (0x1D0040, 0x1D0C34, 0x1D0E40),
        MAGIC_PREVIEW13_CHN (0x1D1040, 0x1D1824, 0x1D1960),
        MAGIC_PREVIEW14_CHN (0x1D1B60, 0x1D2688, 0x1D2888),
        MAGIC_PREVIEW15_CHN (0x1D2A88, 0x1D3460, 0x1D3608);
        
        private final int tilesetPtr;
        private final int tileMapPtr;
        private final int palettePtr;
        
        MagicPreview(int tilesetPtr, int tileMapPtr, int palettePtr)
        {
            this.tilesetPtr = tilesetPtr;
            this.tileMapPtr = tileMapPtr;
            this.palettePtr = palettePtr;
        }

        public int getTilesetPtr() {
            return tilesetPtr;
        }

        public int getTileMapPtr() {
            return tileMapPtr;
        }

        public int getPalettePtr() {
            return palettePtr;
        }

        @Override
        public int getChineseLB() 
        {
            return 16;
        }

        @Override
        public Palette createUncompPalette() throws IOException 
        {
            int[] paletteData = new int[0x200]; //NOT compressed
            writePalette(paletteData, palettePtr, 0, 0x200);
            return new Palette(paletteData);
        }

        @Override
        public ImageSet createImageSet() throws IOException 
        {
            Palette p = createUncompPalette();
            int[] tileset = LempelZiv.decompress(tilesetPtr);
            int[][] tilesetArgs = {new int[0x4000], new int[0x4000], tileset, new int[0x4000]};
            return new ImageSet(p, tilesetArgs);
        }

        @Override
        public BufferedImage[][] createMapImages() throws IOException 
        {
            ImageSet tilesetImages = createImageSet();
            int[] tileMap = LempelZiv.decompress(tileMapPtr);
            return tilesetImages.createTileMap(tileMap, 0x400, 30, 30);
        }

        @Override
        public DrawPanel createDrawPanel(BufferedImage im) throws IOException 
        {
            return new DrawPanel(im.getWidth(), im.getHeight(), ImageIO.read(new File("res/magicPreviewBG.bmp")), im);
        }

        @Override
        public String getFilePath() 
        {
            return "out/" + this.toString() + ".png";
        }
    }
    
    public enum MagicLearnBG0 implements DecompActor
    {
        //sauce: 0803c17c
        MAGIC_LEARN_BG00 (0x1D5908),
        MAGIC_LEARN_BG01 (0x1D59DC),
        MAGIC_LEARN_BG02 (0x1D5AC4),
        MAGIC_LEARN_BG03 (0x1D5B7C),
        MAGIC_LEARN_BG04 (0x1D5C60),
        MAGIC_LEARN_BG05 (0x1D5D34),
        MAGIC_LEARN_BG06 (0x1D5E10),
        MAGIC_LEARN_BG07 (0x1D5EF0),
        MAGIC_LEARN_BG08 (0x1D5FB4),
        MAGIC_LEARN_BG09 (0x1D6048),
        MAGIC_LEARN_BG010 (0x1D60D4),
        MAGIC_LEARN_BG011 (0x1D618C),
        MAGIC_LEARN_BG012 (0x1D6274),
        MAGIC_LEARN_BG013 (0x1D6350),
        MAGIC_LEARN_BG014 (0x1D6424),
        MAGIC_LEARN_BG015 (0x1D64EC),
        
        MAGIC_LEARN_BG00_CHN (0x1D5450),
        MAGIC_LEARN_BG01_CHN (0x1D5530),
        MAGIC_LEARN_BG02_CHN (0x1D55D8),
        MAGIC_LEARN_BG03_CHN (0x1D5678),
        MAGIC_LEARN_BG04_CHN (0x1D5734),
        MAGIC_LEARN_BG05_CHN (0x1D57D4),
        MAGIC_LEARN_BG06_CHN (0x1D5860),
        MAGIC_LEARN_BG07_CHN (0x1D58E8),
        MAGIC_LEARN_BG08_CHN (0x1D5964),
        MAGIC_LEARN_BG09_CHN (0x1D59D4),
        MAGIC_LEARN_BG010_CHN (0x1D5A5C),
        MAGIC_LEARN_BG011_CHN (0x1D5B14),
        MAGIC_LEARN_BG012_CHN (0x1D5BF4),
        MAGIC_LEARN_BG013_CHN (0x1D5CC4),
        MAGIC_LEARN_BG014_CHN (0x1D5D4C),
        MAGIC_LEARN_BG015_CHN (0x1D5DE4);
        
        private int tileMapPtr;
        
        MagicLearnBG0(int tileMapPtr)
        {
            this.tileMapPtr = tileMapPtr;
        }

        @Override
        public int getChineseLB() 
        {
            return 16;
        }

        @Override
        public Palette createUncompPalette() throws IOException 
        {
            int[] paletteData = new int[0x200];
            if (ROM.lang == ROM.JPN)
            {
                writePalette(paletteData, 0x1d548c, 0, 0x20);
                writePalette(paletteData, 0x1d5610, 0x20, 0x20);
                writePalette(paletteData, 0x1d58e8, 0x40, 0x20); //there seem to be different offsets per minigame that have the same palette lul
            }
            else
            {   //Chinese (near 22f50)
                writePalette(paletteData, 0x1d4fd4, 0, 0x20);
                writePalette(paletteData, 0x1d5158, 0x20, 0x20);
                writePalette(paletteData, 0x1d5430, 0x40, 0x20); //again, your offsets may vary
            }
            return new Palette(paletteData);
        }

        @Override
        public ImageSet createImageSet() throws IOException 
        {
            Palette palette = createUncompPalette();
            int[] tileset = null;
            if (ROM.lang == ROM.JPN)
            {
                tileset = LempelZiv.decompress(0x1d3d08);
            }
            else
            {   //Same? Lol
                tileset = LempelZiv.decompress(0x1d3808);
            }
            int[][] tilesetArgs = {tileset};
            return new ImageSet(palette, tilesetArgs);
        }

        @Override
        public BufferedImage[][] createMapImages() throws IOException 
        {
            //still gotta support multiple layers :/
            ImageSet tilesetImages = createImageSet();
            int[] tileMap = LempelZiv.decompress(tileMapPtr);
            return tilesetImages.createTileMap(tileMap, 0, 30, 30);
        }

        @Override
        public DrawPanel createDrawPanel(BufferedImage im) throws IOException 
        {
            return new DrawPanel(im.getWidth(), im.getHeight(), ImageIO.read(new File("res/magicPreviewBG12.BMP")), im);
        }

        @Override
        public String getFilePath() 
        {
            return "out/" + this.toString() + ".png";
        }
    }

    public enum CutsceneBackground implements DecompActor
        {
            SPLASH_VILLAGE(0x19C3A4, 0x4311, 0x1B2A44, 0x1B41B4),
            SPLASH_FLOWER(0x1A06B8, 0x3C02, 0x1B2EF4, 0x1B43B4),
            SPLASH_CLOCK(0x1A42BC, 0x49F6, 0x1B33A4, 0x1B45B4),
            SPLASH_MAGIC(0x1A8CB4, 0x5274, 0x1B3854, 0x1B47B4),
            SPLASH_NEO(0x1ADF28, 0x4B1B, 0x1B3D04, 0x1B49B4),
            SPLASH_CONGRATS(0x1B4BB4, 0x5CE3, 0x1BA898, 0x1BAD48),
            
            CUTSCENE_BG_1(0x1D66D0, 0x339E, 0x1D9A70, 0x1D9F20),
            CUTSCENE_BG_2(0x1DA120, 0x36A2, 0x1DD7C4, 0x1DDC74),
            CUTSCENE_BG_3(0x1DDE74, 0x4644, 0x1E24B8, 0x1E2968),
            CUTSCENE_BG_4(0x1E2B68, 0x38E4, 0x1E644C, 0x1E68FC),
            //UNKNOWN(0x1E97E4, 0xD31, 0x1EA518, 0x1EA9C8),
            
            SPLASH_VILLAGE_CHN(0x19C088, 0x42CE, 0x1B25F8, 0x1B3D68),
            SPLASH_FLOWER_CHN(0x1A0358, 0x3BC5, 0x1B2AA8, 0x1B3F68),
            SPLASH_CLOCK_CHN(0x1A3F20, 0x4A0E, 0x1B2F58, 0x1B4168),
            SPLASH_MAGIC_CHN(0x1A8930, 0x5251, 0x1B3408, 0x1B4368),
            SPLASH_NEO_CHN(0x1ADB84, 0x4A74, 0x1B38B8, 0x1B4568),
            SPLASH_CONGRATS_CHN(0x1B4768, 0x559F, 0x1B9D08, 0x1BA1B8)
            ;

            private int tilesetPtr, tilesetSize, tileMapPtr, palettePtr;

            CutsceneBackground(int tilesetPtr, int tilesetSize, int tileMapPtr, int palettePtr)
            {
                this.tilesetPtr = tilesetPtr;
                this.tilesetSize = tilesetSize;
                this.tileMapPtr = tileMapPtr;
                this.palettePtr = palettePtr;
            }

            @Override
            public int getChineseLB() {
                return 10;
            }

            @Override
            public Palette createUncompPalette() throws IOException 
            {
                int[] paletteData = new int[0x200]; //note - 8-bit palettes may work differently...
                writePalette(paletteData, palettePtr, 0, 0x200);
                return new Palette(paletteData);
            }

            @Override
            public ImageSet createImageSet() throws IOException {
                Palette palette = createUncompPalette();
                int[] tileset = LempelZiv.decompress(tilesetPtr, tilesetSize*4);
                int[][] tilesetArgs = {tileset, new int[0x4000], new int[0x4000], new int[0x4000]};
                return new EightBitDepthImageSet(palette, tilesetArgs);
            }

            @Override
            public BufferedImage[][] createMapImages() throws IOException 
            {
                //still gotta support multiple layers :/
                ImageSet tilesetImages = createImageSet();
                ROM.reader.seek(tileMapPtr);
                byte[] bytes = new byte[0x4B0];
                ROM.reader.readFully(bytes);
                int[] tileMap = new int[0x4B0];
                for (int i = 0; i < tileMap.length; i++)
                {
                    tileMap[i] = (bytes[i] & 0xFF);
                }
                return tilesetImages.createTileMap(tileMap, 0, 30, 30);
            }

            @Override
            public DrawPanel createDrawPanel(BufferedImage im) throws IOException 
            {
                return new DrawPanel(im.getWidth(), im.getHeight(), im);
            }
    }
}
