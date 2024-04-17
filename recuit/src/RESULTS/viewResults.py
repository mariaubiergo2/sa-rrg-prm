from matplotlib import pyplot as plt
import random
from collections import Counter

file = open('src\RESULTS\matrixBigObstacles.txt', 'r')
content = file.readlines()

# Extract objective and remove newline characters
objective = content[0].split('=')[0]
content = content[1:]

# Extract points and edges
points = []
edges = []
edge_labels = []  # List to store labels for edges

for line in content:
    if len(line.split()) == 2:
        if len(edges)<1:
            points.append(tuple(map(int, line.split())))  # Convert to tuple to enable counting
    elif len(line.split()) == 4:
        edge = list(map(int, line.split()))
        edges.append(edge)
        edge_labels.append(f'{edge[0]},{edge[1]} to {edge[2]},{edge[3]}')  # Generate label for edge

# Count occurrences of each point
point_counter = Counter(points)

# Generate random colors for edges
edge_colors = ['#%02x%02x%02x' % (random.randint(0, 255), random.randint(0, 255), random.randint(0, 255)) for _ in range(len(edges))]


# Plot edges with labels and random colors
for edge, label, color in zip(edges, edge_labels, edge_colors):
    x1, y1, x2, y2 = edge
    plt.plot([x1, x2], [y1, y2], marker='o', color=color, label=label)
    
    
# Plot points
for point in points:
    if point_counter[point] > 1:
        plt.plot(point[0], point[1], marker='o', color='blue')
        plt.text(point[0], point[1], s=str(point_counter[point]), fontsize=8, verticalalignment='bottom')
    else:
        plt.plot(point[0], point[1], marker='o', color='blue')

# Set title and labels
plt.title('Objective:' + str(objective) + ' Nedges: '+str(len(edges))+ ' Nnodes: '+str(len(points)))
plt.xlabel('X')
plt.ylabel('Y')

# Add legend
plt.legend()


fileMatrix = open('src\DATA\matrixBigObstacles.txt', 'r')
contentMatrix = fileMatrix.readlines()
y = 0
for line in contentMatrix:
	values = line.split(" ")
	xx = 0
	for x in values:
		if (x=="1"):
			plt.plot(xx, y, ".", color="black")
		xx+=1
	y+=1

# Show plot
plt.grid(True)
plt.show()
