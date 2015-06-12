package model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "players")
public class Player {
	int count=0;
	@Id
	private int id;
	private String firstname;
	private String lastname;
	private String email;
	private String description;
	private Address adress;
	private Sponsor sponsor;
	private List<Integer> opponent = new ArrayList<Integer>();
	Player(){
		
	}
	public Player(String firstname,String lastname,String email){
		super();
		id=count++;
		this.firstname=firstname;
		this.lastname=lastname;
		this.email=email;
		//this.adress=address;
		//this.sponsor=sponsor;
		
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Address getAdress() {
		return adress;
	}
	public void setAdress(Address adress) {
		this.adress = adress;
	}
	public Sponsor getSponsor() {
		return sponsor;
	}
	public void setSponsor(Sponsor sponsor) {
		this.sponsor = sponsor;
	}
	public List<Integer> getOpponent() {
		return opponent;
	}
	public void setOpponent(Integer p_id) {
		opponent.add(p_id);
	}
	
	public boolean isExists(int p_id)
	{
		int i=0;
		for (i=0;i<this.opponent.size();i++){
			if(opponent.get(i).equals(p_id))
			{
				return true;
			}
		}
		return false;
	}
	
	public void removeRelation(int p_id){
		int i;
		for (i=0;i<this.opponent.size();i++){
			if(opponent.get(i).equals(p_id))
			{
				opponent.remove(i);
			}
		}
	}
	@Override
	public String toString() {
		return "Player [id=" + id + ", firstname=" + firstname + ", lastname=" + lastname + ", email=" + email +"]";
	}
}
