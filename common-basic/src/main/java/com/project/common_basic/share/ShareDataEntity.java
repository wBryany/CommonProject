package com.project.common_basic.share;

/**
 * 通用的分享数据模型
 * <p>
 * Created by zcZhang on 15/4/21.
 */
public class ShareDataEntity {

    /**
     * 分享图文中图片url
     */
    private String imgUrl = "";
    /**
     * 图文消息的标题
     */
    private String title = "";
    /**
     * 图文消息的内容
     */
    private String desc = "";
    /**
     * 点击图片消息跳转的链接
     */
    private String link = "";

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "ShareDataEntity{" +
                "imgUrl='" + imgUrl + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
