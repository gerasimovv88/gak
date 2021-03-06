import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";

@Injectable()
export class TimestampService {

  constructor(private http: HttpClient) {
  }

  saveTimestamp(speakerId, timestamps) {
    return this.http.post("/timestamps/" + "?speakerId=" + speakerId, timestamps);
  }

  getTimestampOfSpeaker(speakerId) {
    return this.http.get("/timestamps/speaker" + "?speakerId=" + speakerId);
  }
}

