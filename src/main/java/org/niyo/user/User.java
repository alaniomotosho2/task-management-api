package org.niyo.user;


import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import lombok.*;
import org.niyo.task.Task;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @JsonbTransient
    @OneToMany(mappedBy = "assignee",fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Task> assignedTasks;

    @JsonbTransient
    @OneToMany(mappedBy = "creator",fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Task> createdTasks;

}