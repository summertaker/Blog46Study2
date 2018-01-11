package com.summertaker.blog46study2.parser;

import android.util.Log;

import com.summertaker.blog46study2.common.BaseParser;
import com.summertaker.blog46study2.data.Article;
import com.summertaker.blog46study2.data.Group;
import com.summertaker.blog46study2.data.Member;
import com.summertaker.blog46study2.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class Nogizaka46Parser extends BaseParser {

    public void parseBlogList(String response, Group group, ArrayList<Member> members) {
        /*
        <div id="sidemember">
            <h2>MEMBER</h2>
            <div class="clearfix">
                <div class="unit">
                    <a href="./manatsu.akimoto">
                        <img src="http://img.nogizaka46.com/blog/img/dot.gif" style="background-position:0 0;" width="60" alt="秋元 真夏" />
                        <span class="kanji">秋元 真夏</span>
                        <span class="sub">あきもと まなつ</span>
                    </a>
                </div>
        */

        if (response == null || response.isEmpty()) {
            return;
        }

        //response = Util.getJapaneseString(response, "8859_1");

        Document doc = Jsoup.parse(response);
        Element root = doc.select("#sidemember").first();

        if (root == null) {
            return;
        }

        for (Element div : root.select(".unit")) {
            String id;
            String name;
            String thumbnailUrl;
            String pictureUrl;
            String blogUrl;

            Element a = div.select("a").first();
            if (a == null) {
                continue;
            }

            id = a.attr("href");
            id = id.replace("./", "");
            blogUrl = "http://blog.nogizaka46.com/" + id;

            // 사진을 이미지 맵으로 만들어놔서 데크스탑 프로필 이미지를 가져온다.
            // http://img.nogizaka46.com/www/member/img/akimotomanatsu_prof.jpg

            if (id.contains(".")) {
                String[] array = id.split("\\.");
                if (array.length == 2) {
                    id = array[1] + array[0];
                }
            }

            id = id.replace("eto", "etou");
            id = id.replace("ito", "itou");
            id = id.replace("itouu", "itou");
            id = id.replace("jo", "jou");
            //Log.e(mTag, id);

            thumbnailUrl = "http://img.nogizaka46.com/www/member/img/" + id + "_prof.jpg";
            pictureUrl = thumbnailUrl;

            name = a.select(".kanji").text();

            if (name.contains("期生")) {
                continue;
            }

            //Log.e(mTag, name + " / " + id);

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

        if (response == null || response.isEmpty()) {
            return;
        }

        //response = Util.getJapaneseString(response, "8859_1");

        Document doc = Jsoup.parse(response);
        Element root = doc.select("#sheet").first();

        if (root == null) {
            return;
        }

        //int count = 0;

        for (Element row : root.select("h1")) {
            String title = "";
            String name = "";
            String date = "";
            String html = "";
            String text = "";
            ArrayList<String> images = new ArrayList<>();

            Element el;

            el = row.select(".author").first();
            if (el == null) {
                continue;
            }
            name = el.text();

            el = row.select(".entrytitle").first();
            if (el != null) {
                title = el.text();
            }

            Element sibling = row.nextElementSibling(); // fkd
            if (sibling != null) {
                sibling = sibling.nextElementSibling(); // entrybody
            }
            if (sibling == null) {
                continue;
            }

            text = sibling.text().trim();
            text = text.replace(" ", "");
            text = text.replace("　", "");
            text = text.replaceAll("&nbsp;", "");
            //Log.e(mTag, "text:\n" + text);

            html = sibling.html().trim();

            for (Element img : sibling.select("img")) {
                String src = img.attr("src");
                //src = "http://www.keyakizaka46.com" + src;
                images.add(src);
            }

            //if (i == 0) {
            //    Log.e(mTag, ">>>>> BEFORE\n" + content);
            //}

            /*
            StringBuilder builder =  new StringBuilder();
            String[] array = content.split("</div>");
            int count = 0;
            for (String str : array) {
                Element con = Jsoup.parseBlogList(str);
                String text = con.text().replaceAll(" ", "").trim();
                if (text.isEmpty()) {
                    continue;
                }
                text = (count == 0) ? text : "<br>" + text;
                builder.append(text);

                count++;
            }
            content = builder.toString();
            */

            html = html.replaceAll("&nbsp;", "");
            html = html.replaceAll("<div> </div>", "<br>");
            html = html.replaceAll("<div>([^<|.]*?)</div>", "$1<br>");

            /*
            content = content.replaceAll("<div>", "");
            content = content.replaceAll("</div>", "");

            // img 태그 처리하기
            Element con = Jsoup.parseBlogList(content);
            for (Element e : con.select("img")) {
                Element p = e.parent();
                //Log.e(mTag, p.outerHtml() + "\n" + e.outerHtml());
                content = content.replace(p.outerHtml(), "<p>" + e.outerHtml() + "</p>");
            }

            content = content.replaceAll("<br>\\s*<p>", "");
            content = content.replaceAll("</p>\\s*<br>", "<br><br>");
            */

            html = html.trim();

            // 맨 앞 <br> 잘라내기
            html = html.replaceAll("^(<br>\\s*)+", "").trim();

            // 맨 끝 <br> 잘라내기
            html = html.replaceAll("(<br>\\s*)+$", "").trim();

            // 이중 빈 줄 제거
            // https://stackoverflow.com/questions/3261581/how-to-represent-a-fix-number-of-repeats-in-regular-expression
            html = html.replaceAll("(<br>\\s*){3,}", "<br>").trim(); // 반복

            //if (i == 0) {
            //    Log.e(mTag, ">>>>> AFTER\n" + content);
            //}

            //i++;

            sibling = sibling.nextElementSibling(); // entrybottom
            if (sibling == null) {
                continue;
            }
            String[] dateArray = sibling.text().split("｜");
            date = dateArray[0].trim();
            //date = Util.convertBlogDate(date);

            //Log.e(mTag, title + " / " + name + " / " + date);

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
