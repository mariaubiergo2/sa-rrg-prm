import cv2
import numpy as np

image_path = r"D:\00 TFG AEROS\SA_RRG_S7\S8 - using PRM - STUCK REPRESENTATION\recuit\src\main\java\com\recuit\bangkok\bkkCityScreenshootNoLabels3.png"
image = cv2.imread(image_path)

height, width, _ = image.shape
print("Initial image dimensions:", height, "x", width)

image = cv2.resize(image, (100, 100))
copy = np.zeros((100, 100, 3), dtype=np.uint8)
# copy = np.zeros(image.shape, dtype=np.uint8)

height, width, _ = image.shape
print("Resized image dimensions:", height, "x", width)

# Define thresholds for red, green, and blue channels
red_threshold = 150
green_threshold = 150
blue_threshold = 150

output_file = open(r"D:\00 TFG AEROS\SA_RRG_S7\S8 - using PRM - STUCK REPRESENTATION\recuit\src\main\java\com\recuit\bangkok\bkkMatrix.txt", "w")

# Iterate over each pixel in the image
for y in range(height):
    row_values = []
    for x in range(width):
        b, g, r = image[y, x]
        
        # Calculate the maximum component among red, green, and blue
        max_component = max(r, g, b)

        if r == max_component:
            row_values.append('0')
            if (r==236):
                copy[y, x] = (55, 191, 236)
                
            else:
                copy[y, x] = (0, 0, 255)
        
        elif g == max_component:
            copy[y, x] = (0, 255, 0)
            row_values.append('0')

        elif b == max_component:
            if (b==237 and g==234):
                copy[y, x] = (255, 255, 255)  # the grey
                row_values.append('0')
                # print(f"Pixel at ({x}, {y}): B={b}, G={g}, R={r}")
            else:
                copy[y, x] = (255, 0, 0)
                row_values.append('1')
    
    # Write the row values to the txt file
    output_file.write(' '.join(row_values) + '\n')


output_file.close()


file_path = r"D:\00 TFG AEROS\SA_RRG_S7\S8 - using PRM - STUCK REPRESENTATION\recuit\src\main\java\com\recuit\bangkok\bkkMatrix.txt"

def read_matrix(file_path):
    matrix = []
    with open(file_path, 'r') as fileMatrix:
        for line in fileMatrix:
            matrix.append([int(x) for x in line.split()])
    return matrix

def write_matrix(matrix, file_path):
    with open(file_path, 'w') as file:
        for row in matrix:
            file.write(' '.join(map(str, row)) + '\n')


                
def flip_zeros_and_ones(matrix):
    for i in range(len(matrix)):
        for j in range(len(matrix[0])):
            if matrix[i][j] == 0:
                matrix[i][j] = 1
            else:
                matrix[i][j] = 0

