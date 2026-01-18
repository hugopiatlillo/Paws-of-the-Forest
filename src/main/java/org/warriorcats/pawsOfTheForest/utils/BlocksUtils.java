package org.warriorcats.pawsOfTheForest.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.block.Block;

/**
 * Utility class for block-related operations and environmental checks.
 * 
 * <p>This class provides methods for identifying different types of blocks
 * and checking block-related environmental conditions for gameplay mechanics.</p>
 * 
 * <p>The class is designed as a utility class with static methods only and
 * cannot be instantiated.</p>
 * 
 * @author PawsOfTheForest Team
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class BlocksUtils {

    /**
     * Checks if the given block is a grass block where herbs can be found.
     * 
     * <p>This method checks for basic grass and dirt blocks where herbs typically grow.</p>
     * 
     * @param block the block to check
     * @return true if the block is suitable for herb growth, false otherwise
     */
    public static boolean isGrass(Block block) {
        return switch (block.getType()) {
            case GRASS_BLOCK, 
                 DIRT, 
                 COARSE_DIRT, 
                 ROOTED_DIRT,
                 FARMLAND, 
                 MOSS_BLOCK,
                 PODZOL, 
                 MYCELIUM,
                 SHORT_GRASS,
                 TALL_GRASS,
                 FERN,
                 LARGE_FERN,
                 DANDELION,
                 POPPY,
                 BLUE_ORCHID,
                 ALLIUM,
                 AZURE_BLUET,
                 RED_TULIP,
                 ORANGE_TULIP,
                 WHITE_TULIP,
                 PINK_TULIP,
                 OXEYE_DAISY,
                 CORNFLOWER,
                 LILY_OF_THE_VALLEY,
                 SUNFLOWER,
                 LILAC,
                 ROSE_BUSH,
                 PEONY,
                 SWEET_BERRY_BUSH,
                 DEAD_BUSH -> true;
            default -> false;
        };
    }
}