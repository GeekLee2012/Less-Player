package xyz.less.engine;

public interface Loader<T> {
	T load(String url) throws Exception;
}
