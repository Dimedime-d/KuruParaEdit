import csv

out = ""

def myHex(num):
	return f"0x{num:X}"

with open("MapData.csv") as csvFile:
	reader = csv.reader(csvFile)
	for row in reader:
		if len(row[2]) > 1: #If level to parse
			name = row[2] #Names necessary for Java enums
			tilesetOffsets = [None] * 4
			for k, i in enumerate(range(4, 8)):
				tilesetOffsets[k] = int(row[i], 16) & 0x00FFFFFF
			tileMapOffsets = [None] * 4
			for k, i in enumerate(range(8, 12)):
				tileMapOffsets[k] = int(row[i], 16) & 0x00FFFFFF
			compressedPaletteOffset = int(row[12], 16) & 0x00FFFFFF
			wallPaletteOffset = int(row[14], 16) & 0x00FFFFFF
			out += f"{name.upper()} ({myHex(tilesetOffsets[0])}, {myHex(tilesetOffsets[1])}, {myHex(tilesetOffsets[2])}, {myHex(tilesetOffsets[3])}, " \
				f"{myHex(tileMapOffsets[0])}, {myHex(tileMapOffsets[1])}, {myHex(tileMapOffsets[2])}, {myHex(tileMapOffsets[3])}, " \
				f"{myHex(compressedPaletteOffset)}, {myHex(wallPaletteOffset)}),\n"
	
out += "\n\n"

with open("minigameSplashes.csv") as csvFile:
	reader = csv.reader(csvFile)
	for i, row in enumerate(reader):
		if i == 0: continue
		
		name = f"MINI{i-1}" #Placeholder
		uncompPaletteOffset = int(row[1], 16) & 0x00FFFFFF
		tilesetOffset = int(row[2], 16) & 0x00FFFFFF
		tilesetSize = int(row[3], 16) & 0x00FFFFFF
		tileMapOffset = int(row[4], 16) & 0x00FFFFFF
		out += f"{name.upper()} ({myHex(uncompPaletteOffset)}, {myHex(tilesetOffset)}, {myHex(tilesetSize)}, {myHex(tileMapOffset)}),\n"
		
out += "\n\n"		

with open("minigameSplashesCHN.csv") as csvFile:
	reader = csv.reader(csvFile)
	for i, row in enumerate(reader):
		if i == 0: continue
		
		name = f"MINI{i-1}_CHN" #Placeholder
		uncompPaletteOffset = int(row[1], 16) & 0x00FFFFFF
		tilesetOffset = int(row[2], 16) & 0x00FFFFFF
		tilesetSize = int(row[3], 16) & 0x00FFFFFF
		tileMapOffset = int(row[4], 16) & 0x00FFFFFF
		out += f"{name.upper()} ({myHex(uncompPaletteOffset)}, {myHex(tilesetOffset)}, {myHex(tilesetSize)}, {myHex(tileMapOffset)}),\n"
		
out += "\n\n"

with open("magicPreview.csv") as csvFile:
	reader = csv.reader(csvFile)
	for i, row in enumerate(reader):
		if i == 0: continue
		name = f"MAGIC_PREVIEW{i-1}" #Placeholder
		tilesetPtr = int(row[1], 16) & 0x00FFFFFF
		tileMapPtr = int(row[2], 16) & 0x00FFFFFF
		palettePtr = int(row[3], 16) & 0x00FFFFFF
		out += f"{name.upper()} ({myHex(tilesetPtr)}, {myHex(tileMapPtr)}, {myHex(palettePtr)}),\n"

out += "\n\n"

with open("magicPreviewCHN.csv") as csvFile:
	reader = csv.reader(csvFile)
	for i, row in enumerate(reader):
		if i == 0: continue
		name = f"MAGIC_PREVIEW{i-1}_CHN" #Placeholder
		tilesetPtr = int(row[1], 16) & 0x00FFFFFF
		tileMapPtr = int(row[2], 16) & 0x00FFFFFF
		palettePtr = int(row[3], 16) & 0x00FFFFFF
		out += f"{name.upper()} ({myHex(tilesetPtr)}, {myHex(tileMapPtr)}, {myHex(palettePtr)}),\n"

out += "\n\n"

with open("magicLearnBG0.csv") as csvFile:
	reader = csv.reader(csvFile)
	for i, row in enumerate(reader):
		if i == 0: continue
		name = f"MAGIC_LEARN_BG0{i-1}" #Placeholder
		tileMapPtr = int(row[1], 16) & 0x00FFFFFF
		out += f"{name.upper()} ({myHex(tileMapPtr)}),\n"

out += "\n\n"
		
with open("magicLearnBG0CHN.csv") as csvFile:
	reader = csv.reader(csvFile)
	for i, row in enumerate(reader):
		if i == 0: continue
		name = f"MAGIC_LEARN_BG0{i-1}_CHN" #Placeholder
		tileMapPtr = int(row[1], 16) & 0x00FFFFFF
		out += f"{name.upper()} ({myHex(tileMapPtr)}),\n"
		
with open("getKPLevels out.txt", "w") as file:
	file.write(out)
			
