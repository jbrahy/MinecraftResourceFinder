"""
Block database for Minecraft resource types.
Maps common resource names to their block IDs.
"""

COMMON_RESOURCES = {
    'diamond_ore': 'minecraft:diamond_ore',
    'deepslate_diamond_ore': 'minecraft:deepslate_diamond_ore',
    'ancient_debris': 'minecraft:ancient_debris',
    'emerald_ore': 'minecraft:emerald_ore',
    'deepslate_emerald_ore': 'minecraft:deepslate_emerald_ore',
    'gold_ore': 'minecraft:gold_ore',
    'deepslate_gold_ore': 'minecraft:deepslate_gold_ore',
    'iron_ore': 'minecraft:iron_ore',
    'deepslate_iron_ore': 'minecraft:deepslate_iron_ore',
    'coal_ore': 'minecraft:coal_ore',
    'deepslate_coal_ore': 'minecraft:deepslate_coal_ore',
    'copper_ore': 'minecraft:copper_ore',
    'deepslate_copper_ore': 'minecraft:deepslate_copper_ore',
    'lapis_ore': 'minecraft:lapis_ore',
    'deepslate_lapis_ore': 'minecraft:deepslate_lapis_ore',
    'redstone_ore': 'minecraft:redstone_ore',
    'deepslate_redstone_ore': 'minecraft:deepslate_redstone_ore',
    'nether_quartz_ore': 'minecraft:nether_quartz_ore',
    'nether_gold_ore': 'minecraft:nether_gold_ore',
}

def get_block_id(block_name):
    """
    Convert a block name to its full Minecraft ID.

    Args:
        block_name: Short name like 'diamond_ore' or full ID like 'minecraft:diamond_ore'

    Returns:
        Full block ID with minecraft: namespace
    """
    if block_name in COMMON_RESOURCES:
        return COMMON_RESOURCES[block_name]

    if not block_name.startswith('minecraft:'):
        return f'minecraft:{block_name}'

    return block_name

def list_common_resources():
    """
    Return a list of commonly searched resource names.
    """
    return sorted(COMMON_RESOURCES.keys())
