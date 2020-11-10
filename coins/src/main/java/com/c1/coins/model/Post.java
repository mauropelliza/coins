package com.c1.coins.model;

import java.sql.Timestamp;

public class Post {
	private Integer ID;
	private Integer post_author;
	private Timestamp post_date;
	private Timestamp post_date_gmt;
	private String post_content;
	private String post_title;
	private String post_excerpt;
	private String post_status;
	private String comment_status;
	private String ping_status;
	private String post_password;
	private String post_name;
	private String to_ping;
	private String pinged;
	private Timestamp post_modified;
	private Timestamp post_modified_gmt;
	private String post_content_filtered;
	private Integer post_parent;
	private String guid;
	private Integer menu_order;
	private String post_type;
	private String post_mime_type;
	private Integer comment_count;

	public Post() {

	}

	public Integer getID() {
		return ID;
	}

	public void setID(Integer iD) {
		ID = iD;
	}

	public Integer getPost_author() {
		return post_author;
	}

	public void setPost_author(Integer post_author) {
		this.post_author = post_author;
	}

	public Timestamp getPost_date() {
		return post_date;
	}

	public void setPost_date(Timestamp post_date) {
		this.post_date = post_date;
	}

	public Timestamp getPost_date_gmt() {
		return post_date_gmt;
	}

	public void setPost_date_gmt(Timestamp post_date_gmt) {
		this.post_date_gmt = post_date_gmt;
	}

	public String getPost_content() {
		return post_content;
	}

	public void setPost_content(String post_content) {
		this.post_content = post_content;
	}

	public String getPost_title() {
		return post_title;
	}

	public void setPost_title(String post_title) {
		this.post_title = post_title;
	}

	public String getPost_excerpt() {
		return post_excerpt;
	}

	public void setPost_excerpt(String post_excerpt) {
		this.post_excerpt = post_excerpt;
	}

	public String getPost_status() {
		return post_status;
	}

	public void setPost_status(String post_status) {
		this.post_status = post_status;
	}

	public String getComment_status() {
		return comment_status;
	}

	public void setComment_status(String comment_status) {
		this.comment_status = comment_status;
	}

	public String getPing_status() {
		return ping_status;
	}

	public void setPing_status(String ping_status) {
		this.ping_status = ping_status;
	}

	public String getPost_password() {
		return post_password;
	}

	public void setPost_password(String post_password) {
		this.post_password = post_password;
	}

	public String getPost_name() {
		return post_name;
	}

	public void setPost_name(String post_name) {
		this.post_name = post_name;
	}

	public String getTo_ping() {
		return to_ping;
	}

	public void setTo_ping(String to_ping) {
		this.to_ping = to_ping;
	}

	public String getPinged() {
		return pinged;
	}

	public void setPinged(String pinged) {
		this.pinged = pinged;
	}

	public Timestamp getPost_modified() {
		return post_modified;
	}

	public void setPost_modified(Timestamp post_modified) {
		this.post_modified = post_modified;
	}

	public Timestamp getPost_modified_gmt() {
		return post_modified_gmt;
	}

	public void setPost_modified_gmt(Timestamp post_modified_gmt) {
		this.post_modified_gmt = post_modified_gmt;
	}

	public String getPost_content_filtered() {
		return post_content_filtered;
	}

	public void setPost_content_filtered(String post_content_filtered) {
		this.post_content_filtered = post_content_filtered;
	}

	public Integer getPost_parent() {
		return post_parent;
	}

	public void setPost_parent(Integer post_parent) {
		this.post_parent = post_parent;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public Integer getMenu_order() {
		return menu_order;
	}

	public void setMenu_order(Integer menu_order) {
		this.menu_order = menu_order;
	}

	public String getPost_type() {
		return post_type;
	}

	public void setPost_type(String post_type) {
		this.post_type = post_type;
	}

	public String getPost_mime_type() {
		return post_mime_type;
	}

	public void setPost_mime_type(String post_mime_type) {
		this.post_mime_type = post_mime_type;
	}

	public Integer getComment_count() {
		return comment_count;
	}

	public void setComment_count(Integer comment_count) {
		this.comment_count = comment_count;
	}
}
