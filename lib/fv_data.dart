class FVData {
  String employeeId;
  String imageUrl;

  FVData(this.employeeId, this.imageUrl);

  Map<String, dynamic> toMap() {
    return {
      'employeeId': employeeId,
      'imageUrl': imageUrl,
    };
  }
}
