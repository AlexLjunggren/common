package com.ljunggren.common.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParser {

	public static Document parseHtml(String html) {
		return Jsoup.parse(html);
	}
	
	public static String getValueById(String html, String id) {
		Document parsedHtml = parseHtml(html);
		return getValueById(parsedHtml, id);
	}
	
	public static Element getElementById(String html, String id) {
		Document parsedHtml = parseHtml(html);
		return getElementById(parsedHtml, id);
	}
	
	public static String getValueByName(String html, String name) {
		Document parsedHtml = parseHtml(html);
		return getValueByName(parsedHtml, name);
	}
	
	public static String getLink(String html) {
		return getLink(html, 0);
	}
	
	public static String getLink(String html, int index) {
		Document parsedHtml = parseHtml(html);
		Element link = parsedHtml.select("a").get(index);
		return link.attr("href");
	}
	
	private static String getValueById(Document parsedHtml, String id) {
		Element element = parsedHtml.getElementById(id);
		return element == null ? "" : element.attr("value");
	}
	
	private static Element getElementById(Document parsedHtml, String id) {
		return parsedHtml.getElementById(id);
	}
	
	private static String getValueByName(Document parsedHtml, String name) {
		Element element = parsedHtml.selectFirst("[name=" + name + "]");
		return element == null ? "" : element.attr("value");
	}
	
	public static Elements getTagsByName(String html, String tagName) {
		Document parsedHtml = parseHtml(html);
		return parsedHtml.select(tagName);
	}
	
	public static Elements customSelector(String html, String selector) {
		Document parsedHtml = parseHtml(html);
		return parsedHtml.select(selector);
	}

}
