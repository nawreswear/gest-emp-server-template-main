package tn.iset.m2glnt.server.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "calendar_slots")
public class CalendarSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime time_begin;
    private LocalDateTime time_end;
    private String description;
    private int version;

    public CalendarSlot() {}

    public CalendarSlot(int id, LocalDateTime time_begin, LocalDateTime time_end, String description, int version) {
        this.id = id;
        this.time_begin = time_begin;
        this.time_end = time_end;
        this.description = description;
        this.version = version;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getTime_begin() {
        return time_begin;
    }

    public LocalDateTime getTime_end() {
        return time_end;
    }

    public String getDescription() {
        return description;
    }

    public int getVersion() {
        return version;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTime_begin(LocalDateTime time_begin) {
        this.time_begin = time_begin;
    }

    public void setTime_end(LocalDateTime time_end) {
        this.time_end = time_end;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "CalendarSlot{" +
                "id=" + id +
                ", time_begin=" + time_begin +
                ", time_end=" + time_end +
                ", description='" + description + '\'' +
                ", version=" + version +
                '}';
    }


}