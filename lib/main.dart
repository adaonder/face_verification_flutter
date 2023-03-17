import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:test_face_verification/face_verification_type.dart';
import 'package:test_face_verification/fv_data.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter FV',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Face Verification'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const String channel = "faceDetection";
  static const methodVerify = "METHOD_VERIFY";
  static const String fvData = "FV_DATA";
  static const String fvType = "FV_TYPE";

  String userId = "id_1";
  String imageUrl = "https://profile/your_profile.jpg";

  final platformChannel = const MethodChannel(channel);

  String faceVerificationResult = "";
  TextStyle fvResultTS = const TextStyle(color: Colors.green, fontSize: 22);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: Text(widget.title),
      ),
      body: Center(
        // Center is a layout widget. It takes a single child and positions it
        // in the middle of the parent.
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            const Text(
              'Face Verification Result:',
            ),
            Text(faceVerificationResult, style: fvResultTS),
            const SizedBox(height: 50),
            ElevatedButton(
                onPressed: () async {
                  debugPrint("Test Show");

                  Map<Permission, PermissionStatus> statuses = await [
                    Permission.camera,
                  ].request();
                  debugPrint("$statuses");

                  if (statuses[Permission.camera] != null) {
                    final fvDataModel = FVData(userId, imageUrl);
                    final map = jsonEncode(fvDataModel.toMap());
                    debugPrint("map: $map");

                    platformChannel.invokeMethod(methodVerify, {fvData: map, fvType: FaceVerificationType.BASIC.name}).then((value) {
                      debugPrint("Face Verification Success");
                      setState(() {
                        faceVerificationResult = "Success";
                        fvResultTS = const TextStyle(color: Colors.green, fontSize: 24);
                      });
                    }).onError((PlatformException error, stackTrace) {
                      debugPrint("response:::: error start");
                      debugPrint(error.toString());
                      debugPrint("response:::: error end");
                      setState(() {
                        faceVerificationResult = "${error.message}";
                        fvResultTS = const TextStyle(color: Colors.red, fontSize: 18);
                      });
                    });
                  } else {}
                },
                child: const Text("Start Test"))
          ],
        ),
      ),
    );
  }
}
