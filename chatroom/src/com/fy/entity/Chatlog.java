package com.fy.entity;

import java.sql.Timestamp;

/**
 * @author jack
 */
public class Chatlog {

	private Integer id;
	private Integer fromid;
	private Integer toid;
	private String content;
	private Timestamp sendtime;
	private Integer readstate = 0;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getFromid() {
		return fromid;
	}
	public void setFromid(Integer fromid) {
		this.fromid = fromid;
	}
	public Integer getToid() {
		return toid;
	}
	public void setToid(Integer toid) {
		this.toid = toid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Timestamp getSendtime() {
		return sendtime;
	}
	public void setSendtime(Timestamp sendtime) {
		this.sendtime = sendtime;
	}
	public Integer getReadstate() {
		return readstate;
	}
	public void setReadstate(Integer readstate) {
		this.readstate = readstate;
	}
	public Chatlog(Integer fromid, Integer toid, String content, Timestamp sendtime) {
		super();
		this.fromid = fromid;
		this.toid = toid;
		this.content = content;
		this.sendtime = sendtime;
	}
	
	public Chatlog() {
		
	}
	
}
