package ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.main;

import java.util.List;
import java.util.LinkedList;

import org.hibernate.Session;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashSet;
import java.util.Set;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.dao.*;
import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.dao.impl.*;
import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.model.*;
/***
 *
 * @author DOSE
 * This class implements the communication layer between the persistence and frontend
 * following the singleton patter.
 */

public class LandingPageController {
  /*
   * check that have only one instance of class
   */
  private static LandingPageController instance = null;

  /***
   * Constructor of class LandingPageController
   * Implement the singleton pattern.
   */
  public static LandingPageController getInstance() {
      if(instance == null) {
         instance = new LandingPageController();
      }
      return instance;
  }


  /***
  * * * * * * * * *
  * INDEX METHOD  *
  * * * * * * * * *
  */

  public ModelAndView index(Request req ,Response res){
    Session session = SessionManager.getInstance().openSession();
    BandDAO bandDAO = new BandDaoImpl(session);
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("template", Routes.landing_page());
    return new ModelAndView(attributes, Routes.layout_landing_page());
  }

  /***
  * * * * * * * * *
  * BAND METHODS  *
  * * * * * * * * *
  */

  public JSONObject datatable(Request req,Response res){
    LandingBandsIndexDataTable bandDatatable = new LandingBandsIndexDataTable();
    return bandDatatable.landing(req, res);
  }

  public JSONObject bandID(Request req, Response res){
    JSONObject result = new JSONObject();
    JSONArray artists = new JSONArray();
    JSONArray albums = new JSONArray();

    Session session = SessionManager.getInstance().openSession();
    BandMemberDAO bandMemberDAO = new BandMemberDAOImpl(session);
    BandDAO bdao = new BandDaoImpl(session);
    AlbumDAO adao = new AlbumDaoImpl(session);

    System.out.println("ID: "+req.queryParams("id"));
    Band band = bdao.getBand(req.queryParams("id"));
    // Set<Album> albums = band.getAlbums();


    List<Artist> bandMembers = bandMemberDAO.findByBand(band.getId());

    List<Album> listAlbums = bdao.findAlbums(band.getId().toString());

    System.out.println("SIZE ALBUM : "+listAlbums.size() );


    if(band != null){
      result.put("name", band.getName());
      result.put("genre", band.getGenre());

      for (Artist member : bandMembers) {
        JSONObject artist = new JSONObject();
        artist.put("name",artist_link(member.getName()+" "+member.getSurname(), member.getId()));
        artist.put("nickname",member.getNickname());

        artists.add(artist);
      }
      result.put("members", artists);


      for (Album a : listAlbums) {
        JSONObject album = new JSONObject();
        album.put("title",album_link(a.getTitle(), a.getId()));

        albums.add(album);
      }

      result.put("albums", albums);

    }

    res.type("application/json");
    res.header("Cache-Control", "no-store");
    return result;
  }


  /***
  * * * * * * * * * *
  * ARTIST METHODS  *
  * * * * * * * * * *
  */
  public JSONObject artistID(Request req, Response res){
    Artist artist;
    JSONObject result = new JSONObject();
    JSONArray bands = new JSONArray();

    Session session = SessionManager.getInstance().openSession();
    ArtistDAO adao = new ArtistDaoImpl(session);
    System.out.println("ID: "+req.queryParams("id"));
    artist = adao.getArtist(req.queryParams("id"));

    BandMemberDAO bmDAO = new BandMemberDAOImpl(session);
    List<Band> bandMembers = bmDAO.findByArtist(artist.getId());
    System.out.println("PASEEEE");
    System.out.println("SIZE BANDSMEMEBER: "+ bandMembers.size());



    if(artist != null){
      result.put("name", artist.getName());
      result.put("surname", artist.getSurname());
      result.put("nickname", artist.getNickname());

      for (Band b : bandMembers) {
        JSONObject band = new JSONObject();
        band.put("name",band_link(b.getName(), b.getId()));
        band.put("genre",b.getGenre());

        bands.add(band);
      }
      result.put("bands", bands);

    }

    res.type("application/json");
    res.header("Cache-Control", "no-store");
    return result;
  }

  /***
  * * * * * * * * *
  * ALBUM METHODS *
  * * * * * * * * *
  */
  public JSONObject albumID(Request req, Response res){
    Album album;
    JSONObject result = new JSONObject();
    JSONArray songs = new JSONArray();

    Session session = SessionManager.getInstance().openSession();
    AlbumDAO adao = new AlbumDaoImpl(session);
    System.out.println("ID: "+req.queryParams("id"));
    album = adao.getAlbum(req.queryParams("id"));

    List<Song> song_list = album.getSongs();
    System.out.println("SIZE SONGS : "+song_list.size());

    if(album != null){
      result.put("title", album.getTitle());
      try{
        result.put("release", album.getReleaseDate());
      }catch(Exception e){}

      for (Song s : song_list) {
        JSONObject song = new JSONObject();
        song.put("name",video_link(s.getName(),"link"));
        song.put("duration",s.getDurationAsString());

        songs.add(song);
      }
      result.put("songs",songs);


    }

    res.type("application/json");
    res.header("Cache-Control", "no-store");
    return result;
  }


  /***
  * * * * * * * * *
  * SONG METHODS  *
  * * * * * * * * *
  */

  public JSONObject songID(Request req, Response res){
    Song song;
    JSONObject result = new JSONObject();

    Session session = SessionManager.getInstance().openSession();
    SongDAO sdao = new SongDaoImpl(session);
    System.out.println("ID: "+req.queryParams("id"));
    song = sdao.getSong(req.queryParams("id"));


    if(song != null){
      result.put("name", song.getName());
      result.put("duration", song.getDuration());
    }

    res.type("application/json");
    res.header("Cache-Control", "no-store");
    return result;
  }

  /***
  * * * * * * * * *
  * LINK METHODS  *
  * * * * * * * * *
  */


  public String band_link(String name, String id){
    return "<a class='bands-item' id='"+id+"' href=''>"+name+"</a>";
  }

  public String artist_link(String name, String id){
    return "<a class='artists-item' id='"+id+"' href=''>"+name+"</a>";
  }

  public String album_link(String name, String id){
    return "<a class='albums-item' id='"+id+"' href=''>"+name+"</a>";
  }

  public String song_link(String name, String id){
    return "<a class='songs-item' id='"+id+"' href=''>"+name+"</a>";
  }

  public String video_link(String name, String id){
    return "<a class='"+id+"' href=''>"+name+"</a>";
  }
}
