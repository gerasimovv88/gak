import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";

@Injectable()
export class SpeakerService {

  constructor(private http: HttpClient) {
  }

  saveSpeakersList(speakersList) {
    return this.http.post("/speakers/save", speakersList);
  }

  getSpeakersListOfGroupOfDay(group, date) {
    if (date == null) {
      return this.http.get("/speakers/ofGroupOfDay" + "?group=" + group);
    } else {
      return this.http.get("/speakers/ofGroupOfDay" + "?group=" + group + "&date=" + date);
    }
  }
}

