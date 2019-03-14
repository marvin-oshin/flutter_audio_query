package boaventura.com.devel.br.flutteraudioquery.delegate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import boaventura.com.devel.br.flutteraudioquery.loaders.AlbumLoader;
import boaventura.com.devel.br.flutteraudioquery.loaders.ArtistLoader;
import boaventura.com.devel.br.flutteraudioquery.loaders.GenreLoader;
import boaventura.com.devel.br.flutteraudioquery.loaders.SongLoader;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

/**
 * AudioQueryDelegate makes a validation if a method call can be executed, permission validation and
 * requests and delegates the desired method call to a required loader class where the real call
 * happens in background
 *
 * <p>The work flow in this class is: </p>
 * <p>1) Verify if  already exists a call method to be executed. If there's we finish with a error if not
 *  we go to setp 2.</p>
 *
 *  <p>2) Verify if we have system permissions to run a specific method. If permission is granted we go
 *  to step 3, if not, we make a system permission request and if permission is denied we finish with a
 *  permission_denial error other way we go to step 3.</p>
 *
 *  <p>3) After all validation process we delegate the current method call to a required Loader class
 *  to do a hard work in background. </p>
 *
 */
public class AudioQueryDelegate implements PluginRegistry.RequestPermissionsResultListener,
    AudioQueryDelegateInterface {

    private static final String ERROR_KEY_PENDING_RESULT = "pending_result";
    private static final String ERROR_KEY_PERMISSION_DENIAL = "permission_denial";

    private static final int REQUEST_CODE_PERMISSION_READ_EXTERNAL = 0x01;
    private static final int REQUEST_CODE_PERMISSION_WRITE_EXTERNAL = 0x02;

    private final PermissionManager m_permissionManager;

    private MethodCall m_pendingCall;
    private MethodChannel.Result m_pendingResult;

    private final ArtistLoader m_artistLoader;
    private final AlbumLoader m_albumLoader;
    private final SongLoader m_songLoader;
    private final GenreLoader m_genreLoader;

    public AudioQueryDelegate(final PluginRegistry.Registrar registrar){

        m_artistLoader = new ArtistLoader(registrar.context());
        m_albumLoader = new AlbumLoader(registrar.context());
        m_songLoader = new SongLoader( registrar.context() );
        m_genreLoader = new GenreLoader( registrar.context() );

        m_permissionManager = new PermissionManager() {
            @Override
            public boolean isPermissionGranted(String permissionName) {

                return (ActivityCompat.checkSelfPermission( registrar.activity(), permissionName)
                    == PackageManager.PERMISSION_GRANTED);
            }

            @Override
            public void askForPermission(String permissionName, int requestCode) {
                ActivityCompat.requestPermissions(registrar.activity(), new String[] {permissionName}, requestCode);
            }
        };

        registrar.addRequestPermissionsResultListener(this);
    }


    /**
     * Method used to handle all method calls that is about artist.
     * @param call Method call
     * @param result results input
     */
    @Override
    public void artistSourceHandler(MethodCall call, MethodChannel.Result result){
        if ( canIbeDepedency(call, result)){

            if (m_permissionManager.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE) ){
                clearPendencies();
                handleReadOnlyMethods(call, result);
            }

            else
                m_permissionManager.askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                        REQUEST_CODE_PERMISSION_READ_EXTERNAL);

        } else finishWithAlreadyActiveError(result);

    }


    /**
     * Method used to handle all method calls that is about album data queries.
     * @param call Method call
     * @param result results input
     */
    @Override
    public void albumSourceHandler(MethodCall call, MethodChannel.Result result) {
        if ( canIbeDepedency(call, result)){

            if (m_permissionManager.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE) ){
                clearPendencies();
                handleReadOnlyMethods(call, result);
            }
            else
                m_permissionManager.askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                        REQUEST_CODE_PERMISSION_READ_EXTERNAL);
        } else finishWithAlreadyActiveError(result);
    }

    /**
     * Method used to handle all method calls that is about song data queries.
     * @param call Method call
     * @param result results input
     */
    @Override
    public void songSourceHandler(MethodCall call, MethodChannel.Result result){
        if ( canIbeDepedency(call, result)){

            if (m_permissionManager.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE) ){
                clearPendencies();
                handleReadOnlyMethods(call, result);
            }
            else
                m_permissionManager.askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                        REQUEST_CODE_PERMISSION_READ_EXTERNAL);
        } else finishWithAlreadyActiveError(result);
    }

    /**
     * Method used to handle all method calls that is about genre data queries.
     * @param call Method call
     * @param result results input
     */
    @Override
    public void genreSourceHandler(MethodCall call, MethodChannel.Result result){
        if ( canIbeDepedency(call, result)){

            if (m_permissionManager.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE) ){
                clearPendencies();
                handleReadOnlyMethods(call, result);
            }

            else
                m_permissionManager.askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                        REQUEST_CODE_PERMISSION_READ_EXTERNAL);
        } else finishWithAlreadyActiveError(result);
    }

    /**
     * Method used to handle all method calls that is about playlist.
     * @param call Method call
     * @param result results input
     */
    @Override
    public void playlistSourceHandler(MethodCall call, MethodChannel.Result result){
        // TODO here we'll got two type of methods, read only and write only method
        result.notImplemented();

    }


    /**
     * This method do the real delegate work. After all validation process this method
     * delegates the calls that are read only to a required loader class where all call happen in background.
     * @param call method to be called.
     * @param result results input object.
     */
    private void handleReadOnlyMethods(MethodCall call, MethodChannel.Result result){
        switch (call.method){

            // artists calls section
            case "getArtists":
                m_artistLoader.getArtists(result);
                break;

            case "getArtistsByGenre":
                m_artistLoader.getArtistsByGenre(result, (String)call.argument("genre_name"));
                break;
            //album calls section
            case "getAlbums":
                m_albumLoader.getAlbums(result);
                break;

            case "getAlbumsFromArtist":
                String artist = call.argument("artist" );
                m_albumLoader.getAlbumsFromArtist(result, artist);
                break;


            // song calls section
            case "getSongs":
                m_songLoader.getSongs(result);
                break;

            case "getSongsFromArtist":
                m_songLoader.getSongsFromArtist( result, (String) call.argument("artist" ) );
                break;

            case "getSongsFromAlbum":
                m_songLoader.getSongsFromAlbum( result, (String) call.argument("album_id" ) );
                break;

            // genre calls section
            case "getGenres":
                m_genreLoader.getGenres(result);
                break;
        }

    }

    private void handleWriteOnlyMethods(MethodCall call, MethodChannel.Result result){
        result.notImplemented();
    }


    private boolean canIbeDepedency(MethodCall call, MethodChannel.Result result){

        if ( !setPendingMethodAndCall(call, result) ){
            return false;
        }
        return true;
    }

    private boolean setPendingMethodAndCall(MethodCall call, MethodChannel.Result result){
        //There is something that needs to be delivered...
        if (m_pendingResult != null)
            return false;

        m_pendingCall = call;
        m_pendingResult = result;
        return true;
    }

    private void clearPendencies(){
        m_pendingResult = null;
        m_pendingCall = null;
    }

    private void finishWithAlreadyActiveError(MethodChannel.Result result){
        result.error(ERROR_KEY_PENDING_RESULT,
                "There is some result to be delivered", null);
    }

    private void finishWithError(String errorKey, String errorMsg, MethodChannel.Result result){
        result.error(errorKey, errorMsg, null);
        clearPendencies();
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionGranted = grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        switch (requestCode){

            case REQUEST_CODE_PERMISSION_READ_EXTERNAL:
                if (permissionGranted){
                    handleReadOnlyMethods(m_pendingCall, m_pendingResult);
                    clearPendencies();
                }
                else {
                    finishWithError(ERROR_KEY_PERMISSION_DENIAL,
                            "READ EXTERNAL PERMISSION DENIED", m_pendingResult);
                }
                break;


            case REQUEST_CODE_PERMISSION_WRITE_EXTERNAL:
                if (permissionGranted){
                    handleWriteOnlyMethods(m_pendingCall, m_pendingResult);
                    clearPendencies();
                }

                else {
                    finishWithError(ERROR_KEY_PERMISSION_DENIAL,
                            "WRITE EXTERNAL PERMISSION DENIED", m_pendingResult);
                }
                break;

            default:
                return false;
        }

        return true;
    }

    interface PermissionManager {
        boolean isPermissionGranted(String permissionName);
        void askForPermission(String permissionName, int requestCode);
    }
}

interface AudioQueryDelegateInterface{

    void artistSourceHandler(MethodCall call, MethodChannel.Result result);
    void albumSourceHandler(MethodCall call, MethodChannel.Result result);
    void songSourceHandler(MethodCall call, MethodChannel.Result result);
    void genreSourceHandler(MethodCall call, MethodChannel.Result result);
    void playlistSourceHandler(MethodCall call, MethodChannel.Result result);
}