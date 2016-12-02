package ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.main;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.dao.BandDAO;
import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.dao.impl.*;
import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.model.Band;
import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.model.Artist;
import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.dao.BandMemberDAO;
import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.dao.impl.BandMemberDAOImpl;

import spark.Request;
import spark.Response;
import spark.ModelAndView;

import java.util.HashMap;
import java.util.Map;


/***
 *
 * @author DOSE
 * This class implements the communication layer between the persistence and frontend
 * following the singleton patter.
 */

public class BandController {
  /*
   * check that have only one instance of class
   */
  private static BandController instance = null;

  /***
   * Constructor of class BandController
   * Implement the singleton pattern.
   */
  public static BandController getInstance() {
      if(instance == null) {
         instance = new BandController();
      }
      return instance;
  }

  /***
   * This method returns all bands
   * @param req
   * @param res
   * @return A list of all bands
   */
  public ModelAndView getBands(Request req ,Response res){
      Map<String, Object> attributes = new HashMap<>();

    Session session = SessionManager.getInstance().openSession();
    BandDAO bdao = new BandDaoImpl(session);
    List<Band> bands= bdao.getAllBands();
    session.close();
    // int status = (bands.size()>0)? 200:204;
    // res.status(status);
    // return bands;
    attributes.put("title", "Bandas");
    attributes.put("bands", bands);
    attributes.put("template", Routes.index_band());
    return new ModelAndView(attributes, Routes.layout_dashboard());
  }

  /***
   * This method takes a band name, and returns a list of bands with this name
   * @param req
   * @param res
   * @return a list of bands with the name of the request
   */
  public List<Band> getBandByName(Request req,Response res){
    if (req.params(":name")==""){
      System.out.println("ENTREEE AACACACACACACACACACACA");
      res.status(400);
      return null;
    }
    Session session = SessionManager.getInstance().openSession();
    BandDAO bdao = new BandDaoImpl(session);
    List<Band> bands = bdao.findByName(req.params(":name"));
    session.close();
    int status = (bands.size()!=0)? 200:204;
    res.status(status);
    return bands;
  }

  /***
   * This method takes a band genre and return a list of bands with this genre
   * @param req
   * @param res
   * @return a list of bands with the genre of the request
   */
  public List<Band> getBandByGenre(Request req,Response res){
    if (req.params(":genre")==""){
      res.status(400);
    }
    Session session = SessionManager.getInstance().openSession();
    BandDAO bdao = new BandDaoImpl(session);
    List<Band> bands = bdao.findByGenre(req.params(":genre"));
    session.close();
    int status = (bands.size()!=0)? 200:204;
    res.status(status);
    return bands;
  }

  /**
   * This method take a name and a genre and return a list of bands with this attributes
   * @param req
   * @param res
   * @return a list of bands with the genre and name of the request.
   */
  public List<Band> getBandByNameAndGenre(Request req,Response res){
    if (req.params("genre")==""||req.params("name")==""){
      res.status(400);
    }
    Session session = SessionManager.getInstance().openSession();
    BandDAO bdao = new BandDaoImpl(session);
    List<Band> bands = bdao.findByNameAndGenre(req.params("genre"),req.params("name"));
    session.close();
    int status = (bands.size()!=0)? 200:204;
    res.status(status);
    return bands;
  }

  public ModelAndView showBand(Request req,Response res){
    Map<String, Object> attributes = new HashMap<>();

    Session session = SessionManager.getInstance().openSession();
    BandDAO bandDAO = new BandDaoImpl(session);

    Band band = bandDAO.findById(req.params(":id"));
    attributes.put("band", band);

    attributes.put("template", Routes.show_band());
    attributes.put("title", "Banda");
    return new ModelAndView(attributes, Routes.layout_dashboard());
  }

  public ModelAndView newBand(Request req,Response res){
    Map<String, Object> attributes = new HashMap<>();

    attributes.put("template", Routes.new_band());
    attributes.put("title", "Crear");
    return new ModelAndView(attributes, Routes.layout_dashboard());
  }

  public ModelAndView editBand(Request req,Response res){
    Map<String, Object> attributes = new HashMap<>();

    String id = req.params(":id");
    attributes.put("id", id);

    Session session = SessionManager.getInstance().openSession();
    BandDAO bandDAO = new BandDaoImpl(session);
    Band band = bandDAO.findById(req.params(":id"));

    attributes.put("name", band.getName());
    attributes.put("genre", band.getGenre());

    attributes.put("template", Routes.edit_band());
    attributes.put("title", "Editar");
    return new ModelAndView(attributes, Routes.layout_dashboard());
  }

  /***
   * This method takes the data of a band from the front-end, and creates a band in database
   * @param req
   * @param res
   * @return the object of the band created.
   */
  public ModelAndView createBand(Request req,Response res){
	ModelAndView result;
	Map<String, Object> attributes = new HashMap<>();
    if((req.queryParams("name")=="") || (req.queryParams("genre")=="")){
      res.status(400);
      // return "Request invalid";
      attributes.put("title", "Crear");
      attributes.put("error", "El nombre no puede estar en blanco");
      attributes.put("template", Routes.new_band());
      return new ModelAndView(attributes, Routes.layout_dashboard());
    }
    Session session = SessionManager.getInstance().openSession();
    BandDAO bdao = new BandDaoImpl(session);
    Transaction transaction = null;
    boolean status = false;
    try{
      transaction = session.beginTransaction();
      status = bdao.createBand(req.queryParams("name"),req.queryParams("genre"));
      transaction.commit();
    }catch(HibernateException e){
      transaction.rollback();
      status = false;
      e.printStackTrace();
    }finally {
      session.close();
      if (status){
        res.status(201);
        // return "Success";
        attributes.put("success", "La banda se creo con exito");
        attributes.put("template", Routes.index_band());
        result = new ModelAndView(attributes, Routes.layout_dashboard());
      }
      res.status(409);
      // return "Fail";
      attributes.put("title", "Crear");
      attributes.put("error", "El nombre no puede estar en blanco");
      attributes.put("template", Routes.new_band());
      result= new ModelAndView(attributes, Routes.layout_dashboard());
    }
    return result;
  }

  /***
   * This method takes the data of a band from the front-end, and updates a band in database
   * @param req
   * @param res
   * @return a String that describes the result of update a band.
   */
  public String updateBand(Request req,Response res){
	String result;
    if((req.queryParams("name")=="") && (req.queryParams("genre")=="")){
      res.status(400);
      return "Request invalid";
    }
    Session session = SessionManager.getInstance().openSession();
    BandDAO bdao = new BandDaoImpl(session);
    Band band = bdao.getBand(req.params(":id"));
    if (band==null){
      res.status(400);
      return "Request invalid";
    }
    String bandName = (req.queryParams("name"));
    String bandGenre = (req.queryParams("genre"));
    Transaction transaction = null;
    boolean status = false;
    try{
      transaction = session.beginTransaction();
      status = bdao.updateBand(band.getId(),bandName,bandGenre);
      transaction.commit();
    }catch (HibernateException e) {
      transaction.rollback();
      status = false;
      e.printStackTrace();
    }finally{
      session.close();
      if (status){
        res.status(200);
        result = "Success";
      }
      res.status(409);
      result = "Fail";
    }
    return result;
  }

  /***
   * This method takes the id of a band from the front-end, and delete this band in database
   * @param req
   * @param res
   * @return true if the the band was created. Otherwise, false.
   */
  public String deleteBand(Request req,Response res){
    String result;
	  if ((req.params(":name"))==""){
      res.status();
      return "Request invalid";
    }
    Session session = SessionManager.getInstance().openSession();
    BandDAO bdao = new BandDaoImpl(session);
    Transaction transaction = null;
    boolean status= false;
    try{
      transaction = session.beginTransaction();
      List<Band> searchResult = bdao.findByName(req.params(":name"));
      transaction.commit();
      if (searchResult.size()==1){
        Band toRemove = searchResult.get(0);
        transaction = session.beginTransaction();
        status = bdao.deleteBand(toRemove.getId());
      }
      transaction.commit();
    }catch (HibernateException e) {
      transaction.rollback();
      status = false;
      e.printStackTrace();
    }finally{
      session.close();
      if (status){
        res.status(200);
        result = "Success";
      }
      res.status(409);
      result = "Fail";
    }
    return result;
  }

  /**
   * search Artists of a Band by his name
   * @param req it contain the id of the Band to search Artist
   * @param res
   * @return List of BandMembers
   */
  public List<Artist> getBandMembers(Request req, Response res){
    String bandId = req.params(":bandID");
    if((bandId=="")||(bandId==null)){
      res.status(400);
      return null;
    }
    Session session = SessionManager.getInstance().openSession();
    BandMemberDAO bandMemberDAO = new BandMemberDAOImpl(session);
    List<Artist> bandMembers = bandMemberDAO.findByBand(bandId);
    session.close();
    int status = (bandMembers.size()>0)? 200:204;
    res.status(status);
    return bandMembers;
  }
}
