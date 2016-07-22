package com.theironyard.controlllers;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.model.*;
import com.theironyard.entities.Activity;
import com.theironyard.entities.Restaurant;
import com.theironyard.entities.User;
import com.theironyard.services.ActivityRepository;
import com.theironyard.services.RestaurantRepository;
import com.theironyard.services.UserRepository;
import com.theironyard.utils.PasswordStorage;
import org.h2.index.Index;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

/**
 * Created by hoseasandstrom on 7/19/16.
 */
@RestController
public class MoseyController {
    @Autowired
    UserRepository users;

    @Autowired
    RestaurantRepository restaurants;

    @Autowired
    ActivityRepository activities;

    // start h2 web server
    @PostConstruct
    public void init() throws SQLException, FileNotFoundException {
        Server.createWebServer().start();

        if (restaurants.count() == 0) {
            String filename = "Restaurants.csv";
            File f = new File(filename);
            Scanner filescanner = new Scanner(f);
            filescanner.nextLine();
            while (filescanner.hasNext()) {
                String line = filescanner.nextLine();
                String[] columns = line.split("\\,");
                //int size = columns.length;

                Restaurant restaurant = new Restaurant(columns[0], columns[1], columns[2], columns[3], columns[4]);
                restaurants.save(restaurant);

            }
        }

        if (activities.count() == 0) {
            String filename = "Activities.csv";
            File f = new File(filename);
            Scanner filescanner = new Scanner(f);
            filescanner.nextLine();
            while (filescanner.hasNext()) {
                String line = filescanner.nextLine();
                String[] columns = line.split("\\,");
                //int size = columns.length;

                Activity activity = new Activity(columns[0], columns[1], Boolean.valueOf(columns[2]), columns[3], columns[4]);
                activities.save(activity);

            }
        }


    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String home(Model model, HttpSession session) throws Exception {

        String username = (String) session.getAttribute("username");
        if (username != null) {
            model.addAttribute("user", username);

        }
        GeoApiContext context = new GeoApiContext().
                setApiKey("AIzaSyDdGq0n-cnI--cZyb9gc73KTxEr_mYFVCM");
        GeocodingResult[] results = GeocodingApi.geocode(
                context,
                "Pearlz").await();
        System.out.println(
                results[0].formattedAddress);
        int size = 0;
        while (size< results.length) {
            System.out.println(results[size]);
            size++;
        }
        LatLng ll = new LatLng(32.7784801,-79.9271972);
        NearbySearchRequest a = new NearbySearchRequest(context);

        PlacesSearchResponse result  =  a.location(ll).radius(50).await();
        System.out.println(result);
        //NearbySearchRequest
        return "home";
    }


    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login (HttpSession session, String username, String password) throws Exception {
        User user = users.findByUsername(username);
        if (user == null) {
            return "redirect:/register";
        } else if (!PasswordStorage.verifyPassword(password, user.getPasswordhash())) {
            throw new Exception("Incorrect password");
        }
        session.setAttribute("username", username);

        return "redirect:/";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register (HttpSession session, String firstname,
                            String lastname, String email, String username,
                            String password, boolean isnative) throws PasswordStorage.CannotPerformOperationException {
        User user = users.findByUsername(username);
        if (user == null) {
            user = new User(firstname, lastname, email, username, PasswordStorage.createHash(password), isnative);
            users.save(user);
        }
        return "redirect:/";
    }


}
