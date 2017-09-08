package com.tinyml.structs;

import java.util.TreeMap;

public class WordCount {

	private final TreeMap<String, Integer> map;

	private int count = 0;

	public WordCount() {
		map = new TreeMap<>();
	}

	public void add(String word) {
		Integer val = map.get(word);
		if (val == null) {
			map.put(word, 1);
		} else {
			map.put(word, val + 1);
		}

		count++;
	}

	public int diff() {
		return map.size();
	}

	public int count() {
		return count;
	}

	public int count(String word) {
		Integer count = map.get(word);
		if (count == null) {
			return 0;
		}

		return count;
	}

	public int wordIdx(String word) {

		int idx = 0;
		for (String key : map.keySet()) {

			if (word == key) {
				return idx;
			}
			if (word != null && word.equals(key)) {
				return idx;
			}
			idx++;
		}

		return -1;
	}
}
