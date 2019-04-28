import 'package:flutter/material.dart';
import 'package:flutter_audio_query/flutter_audio_query.dart';
import 'package:flutter_audio_query_example/src/bloc/ApplicationBloc.dart';
import 'package:flutter_audio_query_example/src/bloc/BlocProvider.dart';
import 'package:flutter_audio_query_example/src/ui/screens/PlaylistDetailScreen.dart';
import 'package:flutter_audio_query_example/src/ui/widgets/ListItemWidget.dart';

class PlaylistListWidget extends StatelessWidget {
  
  final List<PlaylistInfo> dataList;
  final MainScreenBloc appBloc;
  PlaylistListWidget({@required this.dataList, @required this.appBloc});
  
  @override
  Widget build(BuildContext context) {

    return ListView.builder(
        itemCount: dataList.length,
        itemBuilder: (context, index) {
          PlaylistInfo playlistInfo = dataList[index];

          return ListItemWidget(
            title: Text(playlistInfo.name),
            subtitle: Text("Total songs: ${playlistInfo.memberIds.length}"),
            onTap: (){ _showPlaylistDetailsScreen(playlistInfo, context); },
            trailing: IconButton(
                icon: Icon(Icons.delete),
                onPressed: (){
                  FlutterAudioQuery.removePlaylist(playlist: playlistInfo);
                  appBloc.loadPlaylistData();
                }
            ),
          );
        }
    );
  }

  void _showPlaylistDetailsScreen(final PlaylistInfo playlist, BuildContext context){

    Navigator.push(context,
      MaterialPageRoute(builder: (context){
        return BlocProvider(
          bloc: PlaylistDetailScreenBloc(playlist),
          child: PlaylistDetailScreen(),
        );
      })
    );
  }
}