package controller;

import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.concurrent.atomic.AtomicLong;

import exceptions.MyExceptions.IdIsNotFoundException;
import controller.User;
import model.Address;
import model.Player;
import model.Sponsor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mongodb.*;

@Controller
@RequestMapping("/Lab3")
public class ApplicationController {

	private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping( method=RequestMethod.GET,value="/")
    public @ResponseBody User sayHello(@RequestParam(value="name", required=false, defaultValue="Stranger") String name) {
    	// For Annotation
    			ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig1.class);
    			MongoOperations mo = (MongoOperations) ctx.getBean("mongoTemplate");

    			User user = new User(name, "password123");

    			// save
    			mo.save(user);

    			// now user object got the created id.
    			System.out.println("1. user : " + user);
    			return user;
    	//return new User(counter.incrementAndGet(), String.format(template, name));
    }
    
    //add the player
    @RequestMapping( method=RequestMethod.POST,value="/player")
    public @ResponseBody Player createPlayer(@RequestParam(value="firstname", required=true) String firstname,
    		@RequestParam(value="lastname", required=true) String lastname,
    		@RequestParam(value="email", required=true) String email,
    		@RequestParam(value="desc", required=false) String desc,
    		@RequestParam(value="street", required=false) String street,
    		@RequestParam(value="city", required=false) String city,
    		@RequestParam(value="state", required=false) String state,
    		@RequestParam(value="zipcode", required=true) String zipcode,
    		@RequestParam(value="sponsor_id",required=false)String sponsor_id) {
    	// For Annotation
  
    			ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig1.class);
    			MongoOperations mo = (MongoOperations) ctx.getBean("mongoTemplate");
    			checkValidPlayer(firstname,lastname,email);
    			Player player =new Player(firstname,lastname,email);
    		    Address address= new Address(street,city,state,zipcode);
    		    player.setAdress(address);
    		    if(sponsor_id!=null){
    		    Query searchQ = new Query(Criteria.where("id").is(Integer.parseInt(sponsor_id)));
    		    try{
    		    	Mongo m = new Mongo();
    		    	DB db = m.getDB("cmpe275");
    		    	DBCollection collection = db.getCollection("players");
    		    	DBObject sort = new BasicDBObject();
    		    	sort.put("_id", -1);
    		    	System.out.println("Hi.");
    		    	DBCursor cursor = collection.find().sort(sort).limit(1);
    		    	while (cursor.hasNext()) {
    		    		System.out.println(cursor.next());
    		    	}
    		    }
    		    catch(UnknownHostException e){
    		    	System.out.println(e);
    		    }
    			// find the saved player again.
    		    Sponsor sponsor = mo.findOne(searchQ, Sponsor.class);
    		    player.setSponsor(sponsor);
    		    }
    		    player.setDescription(desc);
    			// save in mongodb
    			mo.save(player);
    			System.out.println("1. Player added : " + player);
    			return player;
    }
    
    //show players
    @RequestMapping( method=RequestMethod.GET,value="/player/{id}")
    public @ResponseBody Player getPlayer(@PathVariable(value="id")String id,@RequestParam(value="name", required=false, defaultValue="Stranger") String name) {
    	// For Annotation
    			ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig1.class);
    			MongoOperations mo = (MongoOperations) ctx.getBean("mongoTemplate");
    			// query to search player
    			Query searchQ = new Query(Criteria.where("id").is(Integer.parseInt(id)));

    			// find the saved player again.
    			Player savedPlayer = mo.findOne(searchQ, Player.class);
    			if(savedPlayer==null)
    				throw new IdIsNotFoundException();
    			System.out.println("2. find - savedPlayer : " + savedPlayer);
    			return savedPlayer;
    }
    
    //delete player
    @RequestMapping(method=RequestMethod.DELETE,value="/player/{id}")
    public @ResponseBody String deletePlayer(@PathVariable(value="id")String id){
    	ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig1.class);
		MongoOperations mo = (MongoOperations) ctx.getBean("mongoTemplate");
		
		Query searchQ = new Query(Criteria.where("id").is(Integer.parseInt(id)));
		Player p= mo.findOne(searchQ, Player.class);
		if(p==null)
		{
			throw new IdIsNotFoundException();
		}
		mo.remove(searchQ, Player.class);
		return "deleted player of id: "+id;
    }
    
    //update player
    @RequestMapping(method=RequestMethod.PUT,value="/player/{id}")
    public @ResponseBody String updatePlayer(@PathVariable(value="id")String id,
    		@RequestParam(value="firstname", required=true)String newfname,
    		@RequestParam(value="lastname", required=true)String newlname,
    		@RequestParam(value="email", required=true)String email,
    		@RequestParam(value="desc", required=false)String desc,
    		@RequestParam(value="street", required=false) String street,
    		@RequestParam(value="city", required=false) String city,
    		@RequestParam(value="state", required=false) String state,
    		@RequestParam(value="zipcode", required=false) String zipcode,
    		@RequestParam(value="sponsor_id",required=false)String sponsor_id){
    	ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig1.class);
		MongoOperations mo = (MongoOperations) ctx.getBean("mongoTemplate");
		
		Query searchQ = new Query(Criteria.where("id").is(Integer.parseInt(id)));
		Player p= mo.findOne(searchQ, Player.class);
		if(p==null)
		{
			throw new IdIsNotFoundException();
		}
	 mo.updateFirst(searchQ, Update.update("firstname", newfname), Player.class);
	 mo.updateFirst(searchQ, Update.update("lastname", newlname), Player.class);
	 mo.updateFirst(searchQ, Update.update("email", email), Player.class);
	 
	if(desc!=null){
	 mo.updateFirst(searchQ, Update.update("description", desc), Player.class);
	}
	//find the address
	if(street!=null ||city!=null || state!=null || zipcode!=null){
	Query searchQ1 = new Query(Criteria.where("zipcode").is(Integer.parseInt(zipcode)));
	Address a= mo.findOne(searchQ1, Address.class);
	if(a!=null){
		//address found update the address
		if(street!=null)
		  mo.updateFirst(searchQ, Update.update("street", street), Address.class);
		if(city!=null) 
		 mo.updateFirst(searchQ, Update.update("city", city), Address.class);
		if(state!=null) 
		 mo.updateFirst(searchQ, Update.update("state", state), Address.class);
		if(zipcode!=null) 
		 mo.updateFirst(searchQ, Update.update("state", state), Address.class);
	}
	}
	if(sponsor_id!=null)
	{
		//find the sponsor with this id
		Query searchQ2 = new Query(Criteria.where("id").is(Integer.parseInt(sponsor_id)));
		Sponsor s= mo.findOne(searchQ2, Sponsor.class);
		
		if(s!=null)
		  mo.updateFirst(searchQ, Update.update("sponsor", s), Player.class);
		else 
			throw new IdIsNotFoundException();
	}
	 
	 return "Player details updated:ID="+ id;
    }
    
    
    //add the sponsor
    @RequestMapping( method=RequestMethod.POST,value="/sponsor")
    public @ResponseBody Sponsor addSponsor(@RequestParam(value="name", required=true) String name,
    		@RequestParam(value="desc", required=false) String desc,
    		@RequestParam(value="street", required=false) String street,
    		@RequestParam(value="city", required=false) String city,
    		@RequestParam(value="state", required=false) String state,
    		@RequestParam(value="zipcode", required=true) String zipcode) {
    	// For Annotation
    			ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig1.class);
    			MongoOperations mo = (MongoOperations) ctx.getBean("mongoTemplate");
    			//check if required parameter exists,else 400
    			checkValidSponsor(name);
    			Sponsor sponsor = new Sponsor(name);
    			Address address= new Address(street,city,state,zipcode);
    			sponsor.setAddress(address);
    		    sponsor.setDescription(desc);
    		    
    			mo.save(sponsor);
    			
    			System.out.println("Sponsor : " + sponsor+"id="+sponsor.getId());
    			return sponsor;
    }
    
    
  //show Sponsor
    @RequestMapping( method=RequestMethod.GET,value="/sponsor/{id}")
    public @ResponseBody Sponsor getSponsor(@PathVariable(value="id")String id) {
    	// For Annotation
    			ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig1.class);
    			MongoOperations mo = (MongoOperations) ctx.getBean("mongoTemplate");
    			// query to search player
    			Query searchQ = new Query(Criteria.where("id").is(Integer.parseInt(id)));

    			// find the saved player again.
    		Sponsor savedSponsor = mo.findOne(searchQ, Sponsor.class);
    		if(savedSponsor==null)
    		{
    			throw new IdIsNotFoundException();
    		}
    			System.out.println("2. find - savedPlayer : " + savedSponsor);
    			return savedSponsor;
    }
    
    //update sponsor
    @RequestMapping(method=RequestMethod.PUT,value="/sponsor/{id}")
    public @ResponseBody String updateSponsor(@PathVariable(value="id")String id,@RequestParam(value="name", required=true)String newname,
    		@RequestParam(value="desc", required=false)String desc,
    		@RequestParam(value="street", required=false) String street,
    		@RequestParam(value="city", required=false) String city,
    		@RequestParam(value="state", required=false) String state,
    		@RequestParam(value="zipcode", required=false) String zipcode
    		){
    	ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig1.class);
		MongoOperations mo = (MongoOperations) ctx.getBean("mongoTemplate");
	//check if required parameter exists ,else 400
			checkValidSponsor(newname);
	
		Query searchQ = new Query(Criteria.where("id").is(Integer.parseInt(id)));
		Sponsor s= mo.findOne(searchQ, Sponsor.class);
		//check if sponsor exists else 404
		if(s==null)
		{
			throw new IdIsNotFoundException();
		}
		
		if(desc!=null){
			 mo.updateFirst(searchQ, Update.update("description", desc), Sponsor.class);
			}
			//find the address
			if(street!=null ||city!=null || state!=null || zipcode!=null){
			Query searchQ1 = new Query(Criteria.where("zipcode").is(Integer.parseInt(zipcode)));
			Address a= mo.findOne(searchQ1, Address.class);
			if(a!=null){
				//address found update the address
				if(street!=null)
				  mo.updateFirst(searchQ, Update.update("street", street), Address.class);
				if(city!=null) 
				 mo.updateFirst(searchQ, Update.update("city", city), Address.class);
				if(state!=null) 
				 mo.updateFirst(searchQ, Update.update("state", state), Address.class);
				if(zipcode!=null) 
				 mo.updateFirst(searchQ, Update.update("state", state), Address.class);
			}
			}
	 mo.updateFirst(searchQ, Update.update("name", newname), Sponsor.class);
	 
	 return "Sponsor details updated:ID="+ id;
    }
    
    //delete sponsor
    @RequestMapping(method=RequestMethod.DELETE,value="/sponsor/{id}")
    public @ResponseBody String deleteSponsor(@PathVariable(value="id")String id){
    	
    	ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig1.class);
		MongoOperations mo = (MongoOperations) ctx.getBean("mongoTemplate");
		
		Query searchQ = new Query(Criteria.where("id").is(Integer.parseInt(id)));
		Sponsor s= mo.findOne(searchQ, Sponsor.class);
		if(s==null)
		{
			throw new IdIsNotFoundException();
		}
		mo.remove(searchQ, Sponsor.class);
		return "deleted sponsor of user_id: "+id;
    }
    
    //add the oponent
    @RequestMapping(method=RequestMethod.PUT,value="/opponent/{id1}/{id2}")
    public @ResponseBody String addOpponent(@PathVariable(value="id1") String id1,
    		@PathVariable(value="id2")String id2){
    	ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig1.class);
		MongoOperations mo = (MongoOperations) ctx.getBean("mongoTemplate");
		//search the player1
		Query searchQ = new Query(Criteria.where("id").is(Integer.parseInt(id1)));
		Player p1 =mo.findOne(searchQ, Player.class);
		
			//search player2
		Query searchQ2 = new Query(Criteria.where("id").is(Integer.parseInt(id2)));
		Player p2 =mo.findOne(searchQ2, Player.class);
		if(p1==null || p2==null)
			throw new IdIsNotFoundException();
		else if(p1.isExists(p2.getId()) && p2.isExists(p1.getId())){
			return " The players are already oponents of each other";
		}
		else
		{
			p1.setOpponent(p2.getId());
			mo.updateFirst(searchQ, Update.update("opponent", p2.getId()), Player.class);
			p2.setOpponent(p1.getId());
			mo.updateFirst(searchQ2, Update.update("opponent", p1.getId()), Player.class);
		}
		
		return "Players added as the opponents";
    }
    
  //delete sponsor
    @RequestMapping(method=RequestMethod.DELETE,value="/opponent/{id1}/{id2}")
    public @ResponseBody String removeOponent(@PathVariable(value="id1")String id1,
    		@PathVariable(value="id2")String id2){
    	
    	ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig1.class);
		MongoOperations mo = (MongoOperations) ctx.getBean("mongoTemplate");
		
		//search the player1
		Query searchQ = new Query(Criteria.where("id").is(Integer.parseInt(id1)));
		Player p1 =mo.findOne(searchQ, Player.class);
				
		//search player2
		Query searchQ2 = new Query(Criteria.where("id").is(Integer.parseInt(id2)));
		Player p2 =mo.findOne(searchQ2, Player.class);
		if(p1==null || p2==null)
			throw new IdIsNotFoundException();
		else if(!p1.isExists(p2.getId()) && !p2.isExists(p1.getId())){
			throw new IdIsNotFoundException();// player1 is not a opponent if player2 and vice versa
		}else{
			//they exist, remove the opponent relation from their list
			p1.removeRelation(p2.getId());
			mo.updateFirst(searchQ, Update.update("opponent", null), Player.class);
			p2.removeRelation(p1.getId());
			mo.updateFirst(searchQ2, Update.update("opponent", null), Player.class);
		}
		
		return "Removed the opponent relation between the two";
    }
    public void checkValidSponsor(String name){
    	if(name==null){
    		throw new InvalidParameterException();
    	}
    }
    public void checkValidPlayer(String firstname,String lastname,String email){
    	if(firstname==null||lastname==null||email==null){
    		throw new InvalidParameterException();
    	}
    }
}
