import cv2
import numpy as np
from sklearn.cluster import KMeans

def find_major_colors(image_path, num_colors=5):
    # Load the image
    image = cv2.imread(image_path)
    image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)  # Convert BGR to RGB

    # Flatten the image to 1D array
    pixels = image.reshape(-1, 3)

    # Perform K-means clustering to find major colors
    kmeans = KMeans(n_clusters=num_colors)
    kmeans.fit(pixels)

    # Get the RGB values of the cluster centers
    colors = kmeans.cluster_centers_

    return colors.astype(np.uint8)  # Convert float to uint8 for RGB values

def get_color_names(colors):
    # List of common color names
    color_names = np.array(["Red", "Green", "Blue", "Yellow", "Cyan", "Magenta", "Orange", "Purple", "Pink", "Brown"])

    color_list = []
    for color in colors:
        # Find the closest color name based on RGB values
        color_name = color_names[np.argmin(np.linalg.norm(colors - color, axis=1))]
        color_list.append((color_name, color))

    return color_list


def extract_colors(image_path):
    # Read the image
    image = cv2.imread(image_path)

    # Convert the image to a 1D array of pixels
    pixels = image.reshape(-1, 3)

    # Get unique BGR color codes
    unique_colors = np.unique(pixels, axis=0)

    return unique_colors



# Example usage
image_path = r"D:\00 TFG AEROS\SA_RRG_S7\S8 - using PRM - STUCK REPRESENTATION\recuit\src\main\java\com\recuit\bangkok\bkkCityScreenshootNoLabels.png"  # Replace with the path to your image
unique_bgr_colors = extract_colors(image_path)

# Print the unique BGR color codes
print("Unique BGR color codes:")
for color in unique_bgr_colors:
    print(color)






# height, width, channels = image.shape
# print(height)
# print(width)
# print(channels)

# # Create an empty image of the same size as the original image
# copy = np.zeros_like(image)

# # Define thresholds for red, green, and blue channels
# red_threshold = 150
# green_threshold = 150
# blue_threshold = 150

# # Get the dimensions of the image
# height, width, _ = image.shape

# # Iterate over each pixel in the image
# for y in range(height):
#     for x in range(width):
#         # Get the BGR values of the pixel
#         b, g, r = image[y, x]
        
#         # Calculate the maximum component among red, green, and blue
#         max_component = max(r, g, b)

#         # Check if the maximum component is red
#         if r == max_component:
#             # Set the corresponding pixel in the copy image to pure red
#             copy[y, x] = (0, 0, 255)  # BGR color format, so (0, 0, 255) is pure red

#         # Check if the maximum component is green
#         elif g == max_component:
#             # Set the corresponding pixel in the copy image to pure green
#             copy[y, x] = (0, 255, 0)  # BGR color format, so (0, 255, 0) is pure green

#         # Check if the maximum component is blue
#         elif b == max_component:
#             # Set the corresponding pixel in the copy image to pure blue
#             if (b==237 and g==234):
#                 copy[y, x] = (255, 255, 255)  # BGR color format, so (255, 0, 0) is pure blue
#                 # print(f"Pixel at ({x}, {y}): B={b}, G={g}, R={r}")
#             else:
#                 copy[y, x] = (255, 0, 0)


#         # # Check if the pixel is predominantly red
#         # if r > red_threshold and g < green_threshold and b < blue_threshold:
#         #     # Set the corresponding pixel in the copy image to pure red
#         #     copy[y, x] = (0, 0, 255)  # BGR color format, so (0, 0, 255) is pure red

#         # # Check if the pixel is predominantly green
#         # elif g > green_threshold and r < red_threshold and b < blue_threshold:
#         #     # Set the corresponding pixel in the copy image to pure green
#         #     copy[y, x] = (0, 255, 0)  # BGR color format, so (0, 255, 0) is pure green

#         # # Check if the pixel is predominantly blue
#         # elif b > blue_threshold and r < red_threshold and g < green_threshold:
#         #     # Set the corresponding pixel in the copy image to pure blue
#         #     copy[y, x] = (255, 0, 0)  # BGR color format, so (255, 0, 0) is pure blue

# # Display the copy image
# cv2.imshow('Copy Image', copy)
# cv2.waitKey(0)
# cv2.destroyAllWindows()




# # Iterate over each pixel in the image
# # for y in range(height):
# #     for x in range(width):
# #         # Get the BGR values of the pixel
# #         b, g, r = image[y, x]

# #         # Print information about the pixel
# #         # print(f"Pixel at ({x}, {y}): B={b}, G={g}, R={r}")

# # Release the image
# cv2.destroyAllWindows()




# # import osmnx as ox
# # import numpy as np
# # from scipy.sparse import lil_matrix, save_npz

# # print("step 1 - SELECT CITY")

# # # Fetch the street network for Bangkok
# # G = ox.graph_from_place('Bangkok, Thailand', network_type='all')
# # print("STEP 1 DONE WITH THIS NODES: ")
# # print(len(G.nodes))

# # print("step 2 - DO MATRIX")
# # # matrix = np.zeros((len(G.nodes),len(G.nodes)), dtype=np.int8)
# # # matrix = lil_matrix((len(G.nodes),len(G.nodes)), dtype=np.int8)

# # # for u, v, data in G.edges(data=True):
# #     # matrix[u, v] = 1
    
# # # Get the list of nodes in the graph
# # nodes = list(G.nodes())

# # # Create a dictionary to map node IDs to matrix indices
# # node_to_index = {node: index for index, node in enumerate(nodes)}

# # # Create an empty sparse matrix with the size of the number of nodes
# # matrix = lil_matrix((len(nodes), len(nodes)), dtype=np.int8)

# # # Iterate over edges and mark corresponding entries in the matrix as 1
# # for u, v, data in G.edges(data=True):
# #     u_index = node_to_index[u]
# #     v_index = node_to_index[v]
# #     matrix[u_index, v_index] = 1
# #     # matrix[v_index, u_index] = 1  # If the graph is undirected, otherwise skip this line

# # print("STEP 2 DONE")

# # print("step 3 - SAVE")
# # print(matrix)
# # # np.savetxt('bangkokMapMatrix.txt', matrix, fmt='%d')
# # # save_npz('bangkokMapMatrix.npz', matrix, fmt='%d')
# # # save_npz('bangkok_map_matrix.npz', matrix)
# # print("STEP 3 DONE")