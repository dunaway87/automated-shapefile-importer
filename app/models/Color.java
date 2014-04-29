package models;

import javax.persistence.Column;
import javax.persistence.Entity;

import play.db.jpa.Model;
@Entity
public class Color extends Model{
	public int  color_id;
	public String value;
	
}
