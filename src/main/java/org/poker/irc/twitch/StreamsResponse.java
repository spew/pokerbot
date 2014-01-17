package org.poker.irc.twitch;

import org.joda.time.DateTime;

import java.util.List;

public class StreamsResponse {
  private int _total;
  private Links _links;
  private List<Stream> streams;

  public int get_total() {
    return _total;
  }

  public Links get_links() {
    return _links;
  }

  public List<Stream> getStreams() {
    return streams;
  }

  public static class Stream {
    private String name;
    private String broadcaster;
    private long id;
    private String game;
    private Channel channel;
    private List<Team> teams;

    public Channel getChannel() {
      return channel;
    }

    public List<Team> getTeams() {
      return teams;
    }

    public String getName() {
      return name;
    }

    public String getBroadcaster() {
      return broadcaster;
    }

    public long getId() {
      return id;
    }

    public String getGame() {
      return game;
    }

    public long getViewers() {
      return viewers;
    }

    public String getPreview() {
      return preview;
    }

    public Links get_links() {
      return _links;
    }

    private long viewers;
    private String preview;
    private Links _links;
  }

  public static class Team {

    private String _id;
    private String name;
    private String info;
    private String display_name;
    private String created_at;
    private String updated_at;
    private String logo;
    private String banner;
    private String background;
    private Links _links;

    public String get_id() {
      return _id;
    }

    public String getName() {
      return name;
    }

    public String getInfo() {
      return info;
    }

    public String getDisplay_name() {
      return display_name;
    }

    public String getCreated_at() {
      return created_at;
    }

    public String getUpdated_at() {
      return updated_at;
    }

    public String getLogo() {
      return logo;
    }

    public String getBanner() {
      return banner;
    }

    public String getBackground() {
      return background;
    }

    public Links get_links() {
      return _links;
    }
  }
  
  public static class Channel {
    private String status;
    private String display_name;
    private String game;
    private long id;
    private String name;
    private String created_at;
    private String updated_at;
    private String logo;
    private String background;
    private String url;
    private Links _links;

    public String getStatus() {
      return status;
    }

    public String getDisplay_name() {
      return display_name;
    }

    public String getGame() {
      return game;
    }

    public long getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public String getCreated_at() {
      return created_at;
    }

    public String getUpdated_at() {
      return updated_at;
    }

    public String getLogo() {
      return logo;
    }

    public String getBackground() {
      return background;
    }

    public String getUrl() {
      return url;
    }

    public Links get_links() {
      return _links;
    }
  }

  public static class Links {
    private String self;
    private String follows;
    private String commercial;
    private String stream_key;
    private String chat;
    private String features;
    private String subscriptions;
    private String editors;
    private String videos;

    public String getSelf() {
      return self;
    }

    public String getFollows() {
      return follows;
    }

    public String getCommercial() {
      return commercial;
    }

    public String getStream_key() {
      return stream_key;
    }

    public String getChat() {
      return chat;
    }

    public String getFeatures() {
      return features;
    }

    public String getSubscriptions() {
      return subscriptions;
    }

    public String getEditors() {
      return editors;
    }

    public String getVideos() {
      return videos;
    }
  }
}
