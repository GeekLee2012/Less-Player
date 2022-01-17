package xyz.less.graphic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.control.Label;
import xyz.less.bean.Constants;
import xyz.less.bean.Lyric;
import xyz.less.util.DateUtil;

//TODO LyricRender
public class TwoLinesLyricRenderer {
	private Label line1;
	private Label line2;
	private Label highlightLbl; 
	private List<String> timeKeyList = new ArrayList<>();
	private String line1TimeKey;
	private String line2TimeKey;
	private String line3TimeKey;
	private Lyric lyric;
//	public static final String defaultStyleClass = "lyric-line";
//	public static final String defaultHlStyleClass = "lyric-line-current";
	private String styleClass = "lyric-line";
	private String hlStyleClass = "lyric-line-current";
	private Runnable noLyricAction = ()-> {
			setNoLyricText();
		};
	
	public TwoLinesLyricRenderer() {
		
	}
	
	public TwoLinesLyricRenderer(Label line1, Label line2) {
		setLines(line1, line2);
	}
	
	public TwoLinesLyricRenderer setLines(Label line1, Label line2) {
		this.line1 = line1;
		this.line2 = line2;
		Guis.addStyleClass(styleClass, line1, line2);
		return this;
	}
	
	public TwoLinesLyricRenderer setOnNoLyric(Runnable action) {
		if(action != null) {
			this.noLyricAction = action;
		}
		return this;
	}
	
	public TwoLinesLyricRenderer setStyleClass(String value) {
		this.styleClass = value;
		return this;
	}
	
	public TwoLinesLyricRenderer setHighlightStyleClass(String value) {
		this.hlStyleClass = value;
		return this;
	}
	
	public boolean setLyric(Lyric lyric) {
		if(lyric == null) {
			return false;
		}
		this.lyric = lyric;
		timeKeyList.addAll(lyric.getDatas().keySet());
		return hasLyricDatas();
	}
	
	public void render(double currentMinutes) {
		try {
			doRender(currentMinutes);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void doRender(double currentMinutes) {
		if(!hasLyricDatas()) {
			noLyricAction.run();
			return ;
		}
		highlightLbl = line1;
		if(line1TimeKey == null && line2TimeKey == null) {
			updateLyricText(currentMinutes);
		} else {
			double time1 = DateUtil.toMinutes(line1TimeKey);
			double time2 = DateUtil.toMinutes(line2TimeKey);
			double time3 = DateUtil.toMinutes(line3TimeKey);
			if(currentMinutes < time1) {
				updateLyricText(currentMinutes);
			} else if(currentMinutes < time2) {
				highlightLbl = line1;
			} else if(currentMinutes < time3) {
				highlightLbl = line2;
			} else {
				updateLyricText(currentMinutes);
			}
		}
		highlightLyric();
	}
	
	private void highlightLyric() {
		Label[] lines = { line1, line2 };
		Arrays.asList(lines).forEach(line -> {
			Guis.toggleStyleClass(line == highlightLbl, hlStyleClass, line);
		});
	}
	
	private void updateLyricText(double currentMinutes) {
		int currentIndex = getLyricCurrentIndex(currentMinutes);
		line1TimeKey = timeKeyList.get(currentIndex);
		if(currentIndex <= timeKeyList.size() - 3) {
			line2TimeKey = timeKeyList.get(currentIndex + 1);
			line3TimeKey = timeKeyList.get(currentIndex + 2);
		} else if(currentIndex <= timeKeyList.size() - 2) {
			line2TimeKey = timeKeyList.get(currentIndex + 1);
			line3TimeKey = Constants.INFINITED_TIME_KEY;
		} else if(currentIndex <= timeKeyList.size() - 1) {
			line2TimeKey = Constants.INFINITED_TIME_KEY;
			line3TimeKey = Constants.INFINITED_TIME_KEY;
		}
		line1.setText(lyric.getLine(line1TimeKey));
		line2.setText(lyric.getLine(line2TimeKey));
	}
	
	private int getLyricCurrentIndex(double currentMinutes) {
		double offsetMinutes = lyric.getOffset() / 1000.0D / 60.0D;
		int size = timeKeyList.size();
		for(int i = 0; i < size; i++) {
			String key = timeKeyList.get(i);
			double minutes = DateUtil.toMinutes(key);
			double lyricTime = minutes + offsetMinutes;
			if (currentMinutes < lyricTime) {
				return i > 1 ? i - 1 : 0; 
			}
		}
		return size > 1 ? size - 1 : 0;
	}
	
	public void reset() {
		line1TimeKey = null;
		line2TimeKey = null;
		line3TimeKey = null;
		if(timeKeyList != null) {
			timeKeyList.clear();
		}
	}
	
	public boolean hasLyricDatas() {
		return timeKeyList.size() > 0;
	}
	
	protected void setNoLyricText() {
		line1.setText("暂时没发现歌词");
		line2.setText("请继续欣赏音乐吧~");
		Guis.setUserData(null, line1, line2);
		Guis.addStyleClass(styleClass, line1, line2);
	}
}
