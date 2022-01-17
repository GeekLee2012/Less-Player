package xyz.less.apiprovider;

import java.io.ByteArrayInputStream;
import java.util.function.Consumer;

import javafx.scene.image.Image;
import xyz.less.api.IGraphicApi;

public class GraphicApiProvider implements IGraphicApi {
	private Consumer<Image> coverArtConsumer;
	
	@Override
	public void updateCoverArt(byte[] buf) {
		coverArtConsumer.accept(new Image(new ByteArrayInputStream(buf)));
	}

}
