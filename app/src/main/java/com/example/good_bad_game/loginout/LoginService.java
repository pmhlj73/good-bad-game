package com.example.good_bad_game.loginout;

import com.example.good_bad_game.Matching;
import com.example.good_bad_game.friend.getFriend;
import com.example.good_bad_game.readyroom.Room;
import com.example.good_bad_game.readyroom.getRoom;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LoginService {

    @GET("user/")
    retrofit2.Call<List<Login>> getPosts();

    @POST("user/")
    Call<Post> createPost(@Body Post post);

    @GET("friend/")
    retrofit2.Call<List<getFriend>> getFriends();

    @GET("room/")
    retrofit2.Call<List<getRoom>> getRoom();

    @POST("room/")
    Call<Room>  Room(@Body Room room);

    @POST("user-matching/")
    Call<Matching>  Matching(@Body Matching matching);

}
