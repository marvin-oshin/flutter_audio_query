part of flutter_audio_query;

class DataModel {

  /// unique database row register identify.
  static const String ID = "_id";

  /// model data
  final Map<dynamic, dynamic> _data;

  DataModel._(this._data);

  /// The data model id
  String get id => _data[ID] ?? "";

  @override
  String toString() {
    return '$runtimeType($_data)';
  }
}