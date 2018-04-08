package com.search.main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abelard on 2018/4/8.
 */
public class Singers {
    private static String host = "http://www.kuwo.cn";
    private static String url = host + "/artist/indexAjax?category=0&prefix=&pn=%d";

    public static List<String> getSingers() {
        String host = "http://www.kuwo.cn";
        String url = host + "/artist/indexAjax?category=0&prefix=&pn=%d";
        List<String> singer_urls = new ArrayList<>();
        int i = 0;
        int max_len = 10;
        String last = null;
        while (true) {
            String cur_url = String.format(url, i);
            try{
                if(i >= max_len) {
                    break;
                }
                Document doc = Jsoup.connect(cur_url).get();
                String cur = doc.text();
                if(cur.equals(last)) {
                    break;
                }
                Elements a_name = doc.getElementsByClass("a_name");

                for(int i1 = 0; i1 < a_name.size(); i1++) {
                    Element a = a_name.get(i1);
                    String  singer_url = a.attr("href");
                    System.out.println(singer_url);
                    singer_urls.add(singer_url);
                }
                last = cur;
                i++;

            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        return singer_urls;
    }

    public static List<String> getSongs(String singer_url) {
        Document doc = null;
        List<String> song_urls = new ArrayList<>();
        try{
            doc = Jsoup.connect(host + singer_url).get();
            String artistid = doc.getElementsByClass("artistTop").get(0).attr("data-artistid");
            String url = "/artist/contentMusicsAjax?artistId=%s&pn=%d&rn=100";
            int i = 0;
            String last = null;
            while(true) {
                String cur_url = String.format(url, artistid, i);
                doc = Jsoup.connect(host + cur_url).get();
                String cur = doc.text();
                if(cur.equals(last)) {
                    break;
                }
                Elements songs = doc.getElementsByClass("listMusic").get(0).children();
                for(int j = 0; j < songs.size(); j++) {
                    Element song = songs.get(j);
                    String name = song.getElementsByClass("name").get(0).text();
                    String href = song.getElementsByClass("name").get(0)
                                      .getElementsByTag("a").get(0).attr("href");
                    System.out.println("song's href: " + href);
                    song_urls.add(href);
                }
                i++;
                last = cur;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return song_urls;
        }
    }
    public static void main(String[] args) {
        List<String> singers = Singers.getSingers();
        for(String singer : singers) {
            System.out.println("name: " + singer.split("name=")[1]);
            System.out.println("size: " + getSongs(singer).size());
        }
        System.out.println("singers' size: " + singers.size());
    }
}


