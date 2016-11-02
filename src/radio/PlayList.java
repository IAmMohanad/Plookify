package radio;

import java.util.ArrayList;

/**     ****PlayList*****
 * Author: Marcel Riederer Monclou
 * Uses ArrayList to organise MetaData Entries
 * Can add and get entries.
 * Can return lists of artist names and track names
 *
 */
public class PlayList {
    
    ArrayList<MetaData> list = new ArrayList<>();
    int size = 0;
    
    public PlayList(){
    }
    
    public PlayList(MetaData entry){
        list.add(entry);
        size ++;
    }
    
    public void add(MetaData entry){
        list.add(entry);
        size++;
    }
    
    public MetaData get(int x){
        return list.get(x);
    }
    
    public int getSize(){
        return size;
    }
    
    public ArrayList getArtistList(){
        ArrayList<String> list = new ArrayList<>();
        for(int i=0; i<size; i++){
            list.add(this.list.get(i).getArtistName());
        }
        return list;
    }
    
    public ArrayList getTrackNameList(){
        ArrayList<String> list = new ArrayList<>();
        for(int i=0; i<size; i++){
            list.add(this.list.get(i).getTrackName());
        }
        return list;
    }
    
    public void printTrackId(){
        System.out.print("TrackId[");
        for(int i=0; i<size; i++){
            System.out.print( list.get(i).getTrackId() );
            System.out.print(",");
        }
        System.out.println("]");
    }
    
    public void printArtistId(){
        System.out.print("ArtistId[");
        for(int i=0; i<size; i++){
            System.out.print( list.get(i).getArtistId() );
            System.out.print(",");
        }
        System.out.println("]");
    }
    
    public void printTrack(){
        System.out.print("Track[");
        for(int i=0; i<size; i++){
            System.out.print( list.get(i).getTrackName() );
            System.out.print(",");
        }
        System.out.println("]");
    }
    
    public void printArtist(){
        System.out.print("Artist[");
        for(int i=0; i<size; i++){
            System.out.print( list.get(i).getArtistName() );
            System.out.print(",");
        }
        System.out.println("]");
    }
    
    public void printGenre(){
        System.out.print("Genre[");
        for(int i=0; i<size; i++){
            System.out.print( list.get(i).getGenreName() );
            System.out.print(",");
        }
        System.out.println("]");
    }
    
    
}
