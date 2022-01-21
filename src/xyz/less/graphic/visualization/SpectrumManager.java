package xyz.less.graphic.visualization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SpectrumManager {
	private static final List<Class<? extends ISpectrum>> CLS_LIST = new ArrayList<>();
	private Map<Class<? extends ISpectrum>, ISpectrum> cachedMap = new HashMap<>();
	private int index = -1;
	
	static {
		CLS_LIST.add(RectangleSpectrum.class);
		CLS_LIST.add(GridSpectrum.class);
		CLS_LIST.add(PolyLineSpectrum.class);
		CLS_LIST.add(RhythmSpectrum.class);
	}
	
	public ISpectrum prev() {
		return byIndex(--index);
	}
	
	public ISpectrum next() {
		return byIndex(++index);
	}
	
	private ISpectrum byIndex(int value) {
		//循环
		value = value >= 0 ? value : CLS_LIST.size() - 1;
		index = value % CLS_LIST.size();
		return getSpectrum(CLS_LIST.get(index));
	}
	
	public ISpectrum getSpectrum(Class<? extends ISpectrum> cls) {
		ISpectrum cached = cachedMap.get(cls);
		if(cached == null) {
			cached = getInstance(cls);
			cachedMap.put(cached.getClass(), cached);
		}
		return cached;
	}

	//TODO 不够灵活
	private ISpectrum getInstance(Class<? extends ISpectrum> cls) {
		if(cls == GridSpectrum.class) {
			return new GridSpectrum(32, 28);
		} else if(cls == PolyLineSpectrum.class){
			return new PolyLineSpectrum(66, 405); 
		} else if(cls == RhythmSpectrum.class){
			return new RhythmSpectrum(50, 36); 
		}
		return new RectangleSpectrum(66);
	}
	
	
	
}
