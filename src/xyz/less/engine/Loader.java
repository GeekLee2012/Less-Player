package xyz.less.engine;

//TODO 要做什么，还没想好
public interface Loader<T> {
	T load(String url) throws Exception;
}
