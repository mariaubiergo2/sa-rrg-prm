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


        # # Check if the pixel is predominantly red
        # if r > red_threshold and g < green_threshold and b < blue_threshold:
        #     # Set the corresponding pixel in the copy image to pure red
        #     copy[y, x] = (0, 0, 255)  # BGR color format, so (0, 0, 255) is pure red

        # # Check if the pixel is predominantly green
        # elif g > green_threshold and r < red_threshold and b < blue_threshold:
        #     # Set the corresponding pixel in the copy image to pure green
        #     copy[y, x] = (0, 255, 0)  # BGR color format, so (0, 255, 0) is pure green

        # # Check if the pixel is predominantly blue
        # elif b > blue_threshold and r < red_threshold and g < green_threshold:
        #     # Set the corresponding pixel in the copy image to pure blue
        #     copy[y, x] = (255, 0, 0)  # BGR color format, so (255, 0, 0) is pure blue

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

def check_adjacent_zeros(matrix, i, j):
    # adjacent_positions = [(i-1, j), (i+1, j), (i, j-1), (i, j+1), (i-1, j+1), (i+1, j+1), (i-1, j-1), (i+1, j-1)]
    adjacent_positions = [(i-1, j), (i+1, j), (i, j-1), (i, j+1)]
    for x, y in adjacent_positions:
        if x < 0 or y < 0 or x >= len(matrix) or y >= len(matrix[0]) or matrix[x][y] != 0:
            return False
    return True

def replace_adjacent_ones(matrix):
    for i in range(len(matrix)):
        for j in range(len(matrix[0])):
            if matrix[i][j] == 1 and check_adjacent_zeros(matrix, i, j):
                matrix[i][j] = 0
                
def flip_zeros_and_ones(matrix):
    for i in range(len(matrix)):
        for j in range(len(matrix[0])):
            if matrix[i][j] == 0:
                matrix[i][j] = 1
            else:
                matrix[i][j] = 0


matrix = read_matrix(file_path)
replace_adjacent_ones(matrix)
flip_zeros_and_ones(matrix)
write_matrix(matrix, 'modified_matrix.txt')



# Example usage:
# replace_pattern(r"D:\00 TFG AEROS\SA_RRG_S7\S8 - using PRM - STUCK REPRESENTATION\recuit\src\main\java\com\recuit\bangkok\bkkMatrix.txt")




# Display the copy image
cv2.imshow('Copy Image', copy)
cv2.waitKey(0)
cv2.destroyAllWindows()




# Iterate over each pixel in the image
# for y in range(height):
#     for x in range(width):
#         # Get the BGR values of the pixel
#         b, g, r = image[y, x]

#         # Print information about the pixel
#         # print(f"Pixel at ({x}, {y}): B={b}, G={g}, R={r}")




# import osmnx as ox
# import numpy as np
# from scipy.sparse import lil_matrix, save_npz

# print("step 1 - SELECT CITY")

# # Fetch the street network for Bangkok
# G = ox.graph_from_place('Bangkok, Thailand', network_type='all')
# print("STEP 1 DONE WITH THIS NODES: ")
# print(len(G.nodes))

# print("step 2 - DO MATRIX")
# # matrix = np.zeros((len(G.nodes),len(G.nodes)), dtype=np.int8)
# # matrix = lil_matrix((len(G.nodes),len(G.nodes)), dtype=np.int8)

# # for u, v, data in G.edges(data=True):
#     # matrix[u, v] = 1
    
# # Get the list of nodes in the graph
# nodes = list(G.nodes())

# # Create a dictionary to map node IDs to matrix indices
# node_to_index = {node: index for index, node in enumerate(nodes)}

# # Create an empty sparse matrix with the size of the number of nodes
# matrix = lil_matrix((len(nodes), len(nodes)), dtype=np.int8)

# # Iterate over edges and mark corresponding entries in the matrix as 1
# for u, v, data in G.edges(data=True):
#     u_index = node_to_index[u]
#     v_index = node_to_index[v]
#     matrix[u_index, v_index] = 1
#     # matrix[v_index, u_index] = 1  # If the graph is undirected, otherwise skip this line

# print("STEP 2 DONE")

# print("step 3 - SAVE")
# print(matrix)
# # np.savetxt('bangkokMapMatrix.txt', matrix, fmt='%d')
# # save_npz('bangkokMapMatrix.npz', matrix, fmt='%d')
# # save_npz('bangkok_map_matrix.npz', matrix)
# print("STEP 3 DONE")