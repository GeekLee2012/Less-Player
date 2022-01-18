package xyz.less.rpc;

import xyz.less.api.provider.Exporter;
import xyz.less.async.AsyncServices;
import xyz.less.bean.Constants;

public final class RpcService {

	//TODO
	public static void start() {
		RpcServer server = new RpcServer(Constants.RPC_PORT);
		Runtime.getRuntime().addShutdownHook(new Thread(()-> {
			server.close();
		}));
		
//		Exporter.unExport(PlaylistApiProvider.class);
		Exporter.printProviderKeys();
		AsyncServices.submit(() -> {
			server.start();
		});
	}
	
}
