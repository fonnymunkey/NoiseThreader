package noisethreader.handlers;

import net.minecraft.world.gen.NoiseGeneratorOctaves;
import noisethreader.NoiseThreader;
import noisethreader.util.INoiseGeneratorOctaves;
import org.apache.logging.log4j.Level;

import java.util.stream.IntStream;

public abstract class VanillaNoiseHandler {
	
	private static double[] finalNoiseArray;
	
	public static double[] generateVanillaNoiseOctaves(NoiseGeneratorOctaves noiseGeneratorOctaves, double[] noiseArray, int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale, int octaves, int octaveSplitSize, int octaveSplitAmount) {
		finalNoiseArray = noiseArray;
		
		try {
			IntStream.range(0, octaveSplitAmount).parallel().forEach(split -> VanillaNoiseHandler.combineArrays(((INoiseGeneratorOctaves)noiseGeneratorOctaves).noisethreader$generateNoiseOctavesThreaded(null, xOffset, yOffset, zOffset, xSize, ySize, zSize, xScale, yScale, zScale, split * octaveSplitSize, Math.min(octaves, (split + 1) * octaveSplitSize)), xSize, ySize, zSize));
		}
		catch(Exception ex) {
			NoiseThreader.LOGGER.log(Level.ERROR, "NoiseThreader Vanilla Multithreaded Noise encountered an error: " + ex.getMessage(), ex);
			return ((INoiseGeneratorOctaves)noiseGeneratorOctaves).noisethreader$generateNoiseOctavesVanilla(noiseArray, xOffset, yOffset, zOffset, xSize, ySize, zSize, xScale, yScale, zScale);
		}
		
		return finalNoiseArray;
	}
	
	private static synchronized void combineArrays(double[] segmentArray, int xSize, int ySize, int zSize) {
		int i = 0;
		for(int x = 0; x < xSize; ++x) {
			for(int z = 0; z < zSize; ++z) {
				for(int y = 0; y < ySize; ++y) {
					finalNoiseArray[i] += segmentArray[i];
					i++;
				}
			}
		}
	}
}