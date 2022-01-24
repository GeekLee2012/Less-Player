package xyz.less.service;

import xyz.less.api.provider.Exporter;
import xyz.less.api.provider.GraphicApiProvider;
import xyz.less.api.provider.MediaPlayerApiProvider;
import xyz.less.api.provider.PlaylistApiProvider;
import xyz.less.async.AsyncServices;
import xyz.less.bean.AppContext;
import xyz.less.bean.Constants;
import xyz.less.rpc.RpcServer;

public final class RpcService {

	//TODO
	public static void start() {
		RpcServer server = new RpcServer(Constants.DEFAULT_RPC_PORT);
		Runtime.getRuntime().addShutdownHook(new Thread(()-> {
			server.close();
		}));
		
		Exporter.exportObjectsFor(MediaPlayerApiProvider.class, AppContext.get().getMediaService());
		Exporter.unexport(PlaylistApiProvider.class);
		Exporter.unexport(GraphicApiProvider.class);
		Exporter.printProviderKeys();
		
		AsyncServices.submit(() -> {
			server.start();
		});
	}
	
}
