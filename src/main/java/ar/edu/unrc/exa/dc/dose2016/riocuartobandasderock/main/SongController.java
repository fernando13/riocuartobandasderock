package ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.main;



import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.dao.SongDAO;
import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.dao.impl.SessionManager;
import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.dao.impl.SongDaoImpl;
import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.model.Song;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;

import spark.ModelAndView;
import spark.Request;
import spark.Response;


/**
 * This Class implements the song controller
 **/


public class SongController {


    protected static SongController unique_instance = null;

    /**
     * Class constructor
     */

    public static SongController getInstance() {
        if (unique_instance == null)
            unique_instance = new SongController();
        return unique_instance;
    }


    /**
     * Get all bands songs
     * @param req
     * @param res
     * @return a list of all bands songs
     */
    public ModelAndView getAll(Request req, Response res){
      Map<String, Object> attributes = new HashMap<>();
    	Session session = SessionManager.getInstance().openSession();
    	SongDAO sdao = new SongDaoImpl(session);
    	List<Song> songs = sdao.getAllSongs();
    	session.close();
    	// res.status(songs.size() > 0 ? 200 : 204);
        //return songs;

      attributes.put("songs", songs);
      attributes.put("template", Routes.index_song());
      return new ModelAndView(attributes, Routes.layout_dashboard());
    }


    /**
     * Get a specific song by its id
     * @param req contains the Id to search for songs
     * @param res
     * @return the list of songs with Id parameters
     */
    public Song getById (Request req, Response res){
    	
    	Session session = SessionManager.getInstance().openSession();
    	SongDAO sdao = new SongDaoImpl(session);
    	
    	String id = req.params(":id");
    	if (id == ""){
    		res.status(400);
    		return null;
    	}
    	
    	Song song = sdao.findById(id);
    	session.close();
    	res.status(song != null ? 200 : 204);
    	return song;
    }


    /**
     * Get a song by its name
     * @param req contains the name to search for songs
     * @param res
     * @return the list of songs with name parameters
     */
    public List<Song> getByName (Request req, Response res){
    	Session session = SessionManager.getInstance().openSession();
    	SongDAO sdao = new SongDaoImpl(session);
    	
       	String songName = req.params(":name");

    	if (songName == null || songName.isEmpty()){
    		res.status(400);
    		return null;
    	}
    	List<Song> songs = sdao.findByName(songName);
    	session.close();
    	res.status(songs.size() > 0 ? 200 : 204);

    	return songs;
    }

    /**
     * Get list of songs by a specific duration
     * @param req contains the duration to search for songs
     * @param res
     * @return the list of songs with duration parameters
     */
    public List<Song> getByDuration (Request req, Response res){
    	Session session = SessionManager.getInstance().openSession();
    	SongDAO sdao = new SongDaoImpl(session);
    	
    	String duration = req.params(":duration");

    	if (duration == null || duration.isEmpty()){
    		res.status(400);
    		return null;
    	}

    	List<Song> songs = sdao.findByDuration(Integer.parseInt(duration));
    	session.close();

    	res.status(songs.size() > 0 ? 200 : 204);

    	return songs;
    }


    /**
     * Add a new song
     * @param req contains the attributes of the new artist
     * @param res
     * @return a string that describes the result of create
     */
    public ModelAndView create (Request req, Response res){
    	Map<String, Object> attributes = new HashMap<>();
    	String songName = req.queryParams("name");
    	String dur = req.queryParams("duration");
    	Session session = SessionManager.getInstance().openSession();
    	SongDAO sdao = new SongDaoImpl(session);
    	Transaction transaction = session.beginTransaction();
      
      String title = req.queryParams("albumTitle");
      if( songName == null || songName.isEmpty() || dur == null || title == null || title.isEmpty()) {
      res.status(400);
      // res.body("Invalid content of parameters");
      // return res.body();
        attributes.put("error", "El nombre no puede estar en blanco");
        attributes.put("template", Routes.new_song());
        return new ModelAndView(attributes, Routes.layout_dashboard());
      }
      boolean result = sdao.addSong(songName,Integer.parseInt(dur),title);
    	transaction.commit();
    	session.close();
    	if(result){
    	res.body("Song created");
    	// res.status(201);
        attributes.put("success", "La canción se creo con exito");
        attributes.put("template", Routes.index_song());
        return new ModelAndView(attributes, Routes.layout_dashboard());
    	}
      attributes.put("success", "La canción se creo con exito");
      attributes.put("template", Routes.index_song());
      return new ModelAndView(attributes, Routes.layout_dashboard());
    	// return res.body();
    }

    /***
	 * This method takes a song from the frontend, and delete this song in database
	 * @param req contains the Id to search the song and delete it
	 * @param res
	 * @return true if the song was deleted. Otherwise, false.
	 */
	public String remove(Request req,Response res){
		Session session = SessionManager.getInstance().openSession();
		SongDAO sdao = new SongDaoImpl(session);
		String id = req.params(":id");
		if ((id == "") ||(id == null)) {
			res.status(400);
			res.body("Invalid request");
			return res.body();
		}

		Transaction transaction = session.beginTransaction();
	 	boolean status = sdao.removeSong(id);
	 	transaction.commit();
		session.close();

		if (status){
			res.status(200);
			res.body("Success");
			res.body();
		} else {
			res.status(409);
			res.body("Fail");
		}
		return res.body();

	}

	/**
     * Update a song
     * @param req
     * @param res
     * @return a string that describes the result of update
     */

    public String update(Request req, Response res){
    	String id = req.params(":id");
    	if ((id == "") ||(id == null)) {
			res.status(400);
			res.body("Invalid request: Song doesn't exists");
			return res.body();
		}
    	Session session = SessionManager.getInstance().openSession();
    	SongDAO sdao = new SongDaoImpl(session);
    	Song song = sdao.findById(id);
    	session.close();
    	String name = req.queryParams("name");
    	String duration = req.queryParams("duration");
    	String title = req.queryParams("albumTitle");

    	if (name == "" || name == null || duration == null || title.isEmpty() || title == null){
    		res.status(400);
            res.body("Invalid content of parameters");
            return res.body();
    	}


    	song.setName(name);
    	song.setDuration(Integer.parseInt(duration));

    	session = SessionManager.getInstance().openSession();
    	Transaction transaction = session.beginTransaction();
    	boolean status = sdao.updateSong(id, name, (duration.isEmpty()? 0 : Integer.parseInt(duration)), title);
    	transaction.commit();
    	session.close();

    	if (status){
			res.status(200);
			res.body("Success");
			res.body();
		} else {
			res.status(409);
			res.body("Fail");
		}
		return res.body();
    }


    public ModelAndView showSong(Request req,Response res){
    Map<String, Object> attributes = new HashMap<>();

    Session session = SessionManager.getInstance().openSession();
    SongDAO songDAO = new SongDaoImpl(session);

    Song song = songDAO.findById(req.params(":id"));
    attributes.put("song", song);

    attributes.put("template", Routes.show_song());
    attributes.put("title", "show");
    return new ModelAndView(attributes, Routes.layout_dashboard());
  }

  public ModelAndView newSong(Request req,Response res){
    Map<String, Object> attributes = new HashMap<>();

    attributes.put("template", Routes.new_song());
    attributes.put("title", "new");
    return new ModelAndView(attributes, Routes.layout_dashboard());
  }

  public ModelAndView editSong(Request req,Response res){
    Map<String, Object> attributes = new HashMap<>();

    attributes.put("template", Routes.edit_song());
    attributes.put("title", "edit");
    return new ModelAndView(attributes, Routes.layout_dashboard());
  }
}

