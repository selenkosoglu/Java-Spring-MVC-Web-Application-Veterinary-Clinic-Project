package com.works.entities;

import com.works.entities.listener.BaseEntity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class UserFollowIn extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_follow_in_id;

}