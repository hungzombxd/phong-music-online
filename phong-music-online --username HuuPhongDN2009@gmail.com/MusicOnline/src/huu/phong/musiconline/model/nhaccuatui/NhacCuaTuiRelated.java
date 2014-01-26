package huu.phong.musiconline.model.nhaccuatui;
import com.google.gson.annotations.SerializedName;

public class NhacCuaTuiRelated
{

  @SerializedName("Liked")
  public int liked;

  @SerializedName("LinkShare")
  public String linkShare;

  @SerializedName("Listened")
  public int listened;

  @SerializedName("PlaylistCover")
  public String playlistCover;

  @SerializedName("PlaylistId")
  public String playlistId;

  @SerializedName("PlaylistImage")
  public String playlistImage;

  @SerializedName("PlaylistKey")
  public String playlistKey;

  @SerializedName("PlaylistThumb")
  public String playlistThumb;

  @SerializedName("PlaylistTitle")
  public String playlistTitle;

  @SerializedName("Singername")
  public String singername;
}