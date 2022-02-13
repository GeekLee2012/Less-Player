package xyz.less.service;

import xyz.less.api.IGraphicApi;
import xyz.less.api.provider.Exporter;
import xyz.less.api.provider.MediaPlayerApiProvider;
import xyz.less.async.AsyncServices;
import xyz.less.bean.AppContext;
import xyz.less.bean.Constants;
import xyz.less.graphic.Guis;
import xyz.less.rpc.RpcServer;

public final class RpcService {

	//TODO
	public static void start() {
		RpcServer server = new RpcServer(Constants.DEFAULT_RPC_PORT);
		Guis.addShutdownHook(()-> server.close());
		
		Exporter.exportObjectsFor(MediaPlayerApiProvider.class, AppContext.get().getMediaService());
		Exporter.unexportApis(IGraphicApi.class);
		
		Exporter.printAll();

		AsyncServices.submit(() -> {
			server.start();
		});
	}
	
}
