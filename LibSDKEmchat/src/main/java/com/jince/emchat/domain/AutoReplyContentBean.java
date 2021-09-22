package com.jince.emchat.domain;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * <pre>
 *     author : amos
 *     time   : 2019/10/29 17:05
 *     desc   : 自动回复 内容实体
 *     需求: 自动回复配置：支持文字和图片可以关联并配置链接和小程序，即用户点击文字或图片可以跳转到对应的小程序或链接所在页面
 *     version: 1.0
 * </pre>
 */
public class AutoReplyContentBean implements Serializable {
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_TEXT_LINK = "text_link";
    public static final String TYPE_TEXT_MP_LINK = "text_mp_link";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_MP_PAGE = "mp_page";

    /*{
        "id": "2",
            "type": "image",  //图片类型
            "content": {
        "img_url": "www.img.com"
    }
    },
    {
        "id": "3",
            "type": "text_link",  //文字链接类型
            "content": {
        "text": "hehhh",    //文字信息
                "text_link": "www.text_link.com"  //跳转url
    }
    },
    {
        "id": "4",
            "type": "text",   //文本类型
            "content": {
        "text": "文本内容"   //文本内容
    }
    },
    {
        "id": "5",
            "type": "text_mp_link",   //文字链接小程序类型
            "content": {
        "text": "文本内容1",   //文字信息
                "mp_link": "/page/index"   //小程序跳转url
    }
    },
    {
        "id": "6",
            "type": "mp_page",   //小程序卡片
            "content": {
        "mp_link": "/page/index",   //小程序跳转url
                "mp_page_cover_url": "wwww.image.png"   //小程序卡片封面图
    }
    }
    ]*/

    private String id;
    private String type;
    private Content content;

    /**
     * 是否是文本类型
     *
     * @return
     */
    public boolean isText() {
        return TextUtils.equals(TYPE_TEXT, type) || TextUtils.equals(TYPE_TEXT_LINK, type) || TextUtils.equals(TYPE_TEXT_MP_LINK, type);
    }

    /**
     * 是否是图片类型
     *
     * @return
     */
    public boolean isImage() {
        return TextUtils.equals(TYPE_IMAGE, type) || TextUtils.equals(TYPE_MP_PAGE, type);
    }

    //获取文本内容
    public String getTextWord() {
        if (isText() && content != null) {
            return TextUtils.isEmpty(content.getText()) ? "" : content.getText();
        }
        return "";
    }

    public String getImageUrl() {
        if (isImage() && content != null) {
            if (isTypeImage()) {
                return content.getImg_url();
            } else if (isTypeMpPage()) {
                return content.getMp_page_cover_url();
            }
        }
        return "";
    }

    /**
     * //图片类型
     *
     * @return
     */
    public boolean isTypeImage() {
        return TextUtils.equals(TYPE_IMAGE, type);
    }

    /**
     * //文字链接类型
     *
     * @return
     */
    public boolean isTypeTextLink() {
        return TextUtils.equals(TYPE_TEXT_LINK, type);
    }

    /**
     * //文本类型
     *
     * @return
     */
    public boolean isTypeText() {
        return TextUtils.equals(TYPE_TEXT, type);
    }

    /**
     * //文字链接小程序类型
     *
     * @return
     */
    public boolean isTypeTextMpLink() {
        return TextUtils.equals(TYPE_TEXT_MP_LINK, type);
    }

    /**
     * //小程序卡片
     *
     * @return
     */
    public boolean isTypeMpPage() {
        return TextUtils.equals(TYPE_MP_PAGE, type);
    }

    public static class Content implements Serializable {
        /*"content": {
            "img_url": "www.img.com"
        }*/
        private String img_url;
        private String text;
        private String text_link;
        private String mp_link;
        private String mp_page_cover_url;

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getText_link() {
            return text_link;
        }

        public void setText_link(String text_link) {
            this.text_link = text_link;
        }

        public String getMp_link() {
            return mp_link;
        }

        public void setMp_link(String mp_link) {
            this.mp_link = mp_link;
        }

        public String getMp_page_cover_url() {
            return mp_page_cover_url;
        }

        public void setMp_page_cover_url(String mp_page_cover_url) {
            this.mp_page_cover_url = mp_page_cover_url;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}
