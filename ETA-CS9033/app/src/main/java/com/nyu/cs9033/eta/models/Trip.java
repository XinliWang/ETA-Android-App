package com.nyu.cs9033.eta.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;

public class Trip implements Parcelable {
	
	// Member fields should exist here, what else do you need for a trip?
	// Please add additional fields
    private int id;
	private String name;
	private String destination;
	private Calendar time;
    private ArrayList<Person> friends;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
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
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(p.readLong());
        time = cal;
        friends = convertStringToList(p.readString());
	}
	
	/**
	 * Create a Trip model object from arguments
	 * 
	 * @param name  Add arbitrary number of arguments to
	 * instantiate Trip class based on member variables.
	 */
	public Trip(String name,String destination,Calendar time,ArrayList<Person> friends) {
		
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
        dest.writeLong(this.time.getTimeInMillis());
        dest.writeString(convertListToString(this.friends));
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

    /**
     * Convert String to list
     */
    public ArrayList<Person> convertStringToList(String string){
        String[] array = string.split(",");
        ArrayList<Person> list = new ArrayList<Person>();
        for(String a : array){
            Person person = new Person(a);
            list.add(person);
        }
        return list;
    }

    /**
     * Convert list to String
     */
    public String convertListToString(ArrayList<Person> friends){
        String people ="";
        int i=0;
        while(i<friends.size()){
            if(i==0){
                people = friends.get(i).getName();
            }else{
                people = people + "," + friends.get(i).getName();
            }
            i++;
        }

        return people;
    }

}

