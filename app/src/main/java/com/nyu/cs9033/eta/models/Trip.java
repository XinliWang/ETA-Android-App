package com.nyu.cs9033.eta.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Trip implements Parcelable {
	
	// Member fields should exist here, what else do you need for a trip?
	// Please add additional fields
	private String name;
	private String destination;
	private String time;
    private ArrayList<Person> friends;
    private Person person;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<Person> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Person> friends) {
        this.friends = friends;
    }

    /**
	 * Parcelable creator. Do not modify this function.
	 */
	public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
		public Trip createFromParcel(Parcel p) {
			return new Trip(p);
		}

		public Trip[] newArray(int size) {
			return new Trip[size];
		}
	};
	
	/**
	 * Create a Trip model object from a Parcel. This
	 * function is called via the Parcelable creator.
	 * 
	 * @param p The Parcel used to populate the
	 * Model fields.
	 */
	public Trip(Parcel p) {
		
		// TODO - fill in here
        name = p.readString();
        destination = p.readString();
        time = p.readString();
        friends = convertToList(p.readString());
	}
	
	/**
	 * Create a Trip model object from arguments
	 * 
	 * @param name  Add arbitrary number of arguments to
	 * instantiate Trip class based on member variables.
	 */
	public Trip(String name,String destination,String time,ArrayList<Person> friends) {
		
		// TODO - fill in here, please note you must have more arguments here
        this.name = name;
        this.destination = destination;
        this.time = time;
        this.friends = friends;
	}

	/**
	 * Serialize Trip object by using writeToParcel. 
	 * This function is automatically called by the
	 * system when the object is serialized.
	 * 
	 * @param dest Parcel object that gets written on 
	 * serialization. Use functions to write out the
	 * object stored via your member variables. 
	 * 
	 * @param flags Additional flags about how the object 
	 * should be written. May be 0 or PARCELABLE_WRITE_RETURN_VALUE.
	 * In our case, you should be just passing 0.
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		// TODO - fill in here
        dest.writeString(this.name);
        dest.writeString(this.destination);
        dest.writeString(this.time);
        dest.writeString(convertToString(this.friends));
	}
	
	/**
	 * Feel free to add additional functions as necessary below.
	 */
    public Trip(){

    }
    /**
	 * Do not implement
	 */
	@Override
	public int describeContents() {
		// Do not implement!
		return 0;
	}

    private ArrayList<Person> convertToList(String friends){
        
    }

    private String convertToString(ArrayList<Person> friends){

    }

}

