package com.summertaker.blog46study2.parser;

import android.util.Log;

import com.summertaker.blog46study2.common.BaseParser;
import com.summertaker.blog46study2.data.Group;
import com.summertaker.blog46study2.data.Member;
import com.summertaker.blog46study2.data.Article;
import com.summertaker.blog46study2.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Keyakizaka46Parser extends BaseParser {

    public void parseBlogList(String response, Group group, ArrayList<Member> members) {
        /*
        <ul class="thumb">
            <li class="border-06h" data-member="02">
              <a href="/s/k46o/diary/member/list?ima=0000&ct=02">
                    <p>

                      <img src="http://cdn.keyakizaka46.com/images/14/514/2fe0c906fce3bf0869c19c764b345/200_200_102400.jpg" />

                    </p>
                    <p class="name">
                      今泉 佑唯
                    </p>
                </a>
            </li>
        */

        if (response == null || response.isEmpty()) {
            return;
        }

        //response = Util.getJapaneseString(response, "8859_1");

        Document doc = Jsoup.parse(response);
        Element root = doc.select(".thumb").first();

        if (root == null) {
            return;
        }

        for (Element li : root.select("li")) {
            String name;
            String thumbnailUrl;
            String pictureUrl;
            String blogUrl;

            Element a = li.select("a").first();
            if (a == null) {
                continue;
            }
            blogUrl = "http://www.keyakizaka46.com" + a.attr("href");

            Element img = a.select("img").first();
            if (img == null) {
                continue;
            }
            thumbnailUrl = img.attr("src");
            pictureUrl = thumbnailUrl;

            name = a.select("p.name").text();

            if (name.contains("期生")) {
                continue;
            }

            //Log.e(mTag, name);

            Member member = new Member();
            member.setGroupId(group.getId());
            member.setGroupName(group.getName());
            member.setName(name);
            member.setThumbnailUrl(thumbnailUrl);
            member.setPictureUrl(pictureUrl);
            member.setBlogUrl(blogUrl);

            members.add(member);
        }
    }

    public void parseBlogDetail(String response, ArrayList<Article> articles) {
        /*
        <ul class="thumb">
            <li class="border-06h" data-member="02">
              <a href="/s/k46o/diary/member/list?ima=0000&ct=02">
                    <p>

                      <img src="http://cdn.keyakizaka46.com/images/14/514/2fe0c906fce3bf0869c19c764b345/200_200_102400.jpg" />

                    </p>
                    <p class="name">
                      今泉 佑唯
                    </p>
                </a>
            </li>
        */

        if (response == null || response.isEmpty()) {
            return;
        }

        //response = Util.getJapaneseString(response, "8859_1");

        Document doc = Jsoup.parse(response);
        Element root = doc.select(".box-main").first();

        if (root == null) {
            return;
        }

        //int count = 0;

        for (Element row : root.select("article")) {
            String title = "";
            String name = "";
            String date = "";
            String html = "";
            String text = "";
            ArrayList<String> images = new ArrayList<>();

            Element el;

            Element d = row.select(".box-date").first();
            if (d == null) {
                continue;
            }
            el = d.select("time").first();
            if (el != null) {
                date = el.text();
                el = d.select("time").last();
                if (el != null) {
                    date += "." + el.text();
                }
            }

            Element ttl = row.select(".box-ttl").first();
            if (ttl == null) {
                continue;
            }
            el = ttl.select("h3").first();
            el = el.select("a").first();
            title = el.html().trim();
            if (title.isEmpty()) {
                title = "無題";
            }

            el = ttl.select(".name").first();
            name = el.text().trim();

            el = row.select(".box-article").first();

            text = el.text().trim();
            text = text.replace(" ", "");
            text = text.replace("　", "");
            text = text.replaceAll("&nbsp;", "");
            //Log.e(mTag, "text:\n" + text);

            //if (i == 0) {
            //    Log.e(mTag, ">>>>> BEFORE\n" + content);
            //}

            for (Element img : el.select("img")) {
                String src = img.attr("src");
                //src = "http://www.keyakizaka46.com" + src;
                images.add(src);
            }

            html = el.html().trim();
            String[] array = html.split("<br>");
            StringBuilder builder = new StringBuilder();
            int lineCount = 0;
            for (String str : array) {
                Element con = Jsoup.parse(str);
                Element img = con.select("img").first();
                String temp = "";
                if (img != null) {
                    String src = img.attr("src");
                    temp = "<a href=\"" + src + "\">" + img.outerHtml() + "</a>";
                } else {
                    temp = con.text().trim();
                    if (temp.isEmpty()) {
                        continue;
                    }
                }

                temp = (lineCount == 0) ? temp : "<br><br>" + temp;
                builder.append(temp);

                lineCount++;
            }
            html = builder.toString();

            /*
            // 맨 앞 <br> 잘라내기
            content = content.replaceAll("^(<br>\\s*)+", "").trim();

            // 맨 끝 <br> 잘라내기
            content = content.replaceAll("(<br>\\s*)+$", "");

            // 이중 빈 줄 제거
            // https://stackoverflow.com/questions/3261581/how-to-represent-a-fix-number-of-repeats-in-regular-expression
            content = content.replaceAll("(\\s*<br>\\s*){3,}", "<br>"); // 반복

            // img 태그 처리하기
            el = Jsoup.parseBlogList(content);
            for (Element e : el.select("img")) {
                content = content.replace(e.outerHtml(), "<p>" + e.outerHtml() + "</p>");
            }
            */

            //if (i == 0) {
            //    Log.e(mTag, ">>>>> AFTER\n" + content);
            //}

            //i++;

            el = row.select(".box-bottom").first();
            el = el.select("ul").first();
            el = el.select("li").first();
            date = el.text();
            //date = Util.convertBlogDate(date);

            //Log.e(mTag, ">> " + title + "\n" + text);

            Article article = new Article();
            article.setTitle(title);
            article.setName(name);
            article.setDate(date);
            article.setHtml(html);
            article.setText(text);
            article.setImages(images);

            articles.add(article);

            //count++;

            //if (count >= 1) {
            //    break;
            //}
        }
    }
}
