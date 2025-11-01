"""
Navigation and distance calculation utilities.
"""

import math

def calculate_distance(pos1, pos2):
    """
    Calculate 3D Euclidean distance between two positions.

    Args:
        pos1: Tuple of (x, y, z)
        pos2: Tuple of (x, y, z)

    Returns:
        Distance as float
    """
    x1, y1, z1 = pos1
    x2, y2, z2 = pos2

    return math.sqrt((x2 - x1)**2 + (y2 - y1)**2 + (z2 - z1)**2)

def calculate_horizontal_distance(pos1, pos2):
    """
    Calculate 2D horizontal distance (ignoring Y coordinate).

    Args:
        pos1: Tuple of (x, y, z)
        pos2: Tuple of (x, y, z)

    Returns:
        Horizontal distance as float
    """
    x1, _, z1 = pos1
    x2, _, z2 = pos2

    return math.sqrt((x2 - x1)**2 + (z2 - z1)**2)

def get_direction(from_pos, to_pos):
    """
    Calculate cardinal direction from one position to another.

    Args:
        from_pos: Tuple of (x, y, z) - starting position
        to_pos: Tuple of (x, y, z) - target position

    Returns:
        String like 'North', 'Southeast', etc.
    """
    x1, _, z1 = from_pos
    x2, _, z2 = to_pos

    dx = x2 - x1
    dz = z2 - z1

    angle = math.degrees(math.atan2(dx, -dz))

    if angle < 0:
        angle += 360

    directions = [
        'North', 'North-Northeast', 'Northeast', 'East-Northeast',
        'East', 'East-Southeast', 'Southeast', 'South-Southeast',
        'South', 'South-Southwest', 'Southwest', 'West-Southwest',
        'West', 'West-Northwest', 'Northwest', 'North-Northwest'
    ]

    index = int((angle + 11.25) / 22.5) % 16
    return directions[index]

def sort_by_distance(locations, player_pos):
    """
    Sort a list of locations by distance from player position.

    Args:
        locations: List of tuples (x, y, z)
        player_pos: Tuple of (x, y, z)

    Returns:
        Sorted list of tuples (distance, x, y, z)
    """
    results = []
    for loc in locations:
        dist = calculate_distance(player_pos, loc)
        results.append((dist, loc[0], loc[1], loc[2]))

    return sorted(results, key=lambda x: x[0])
