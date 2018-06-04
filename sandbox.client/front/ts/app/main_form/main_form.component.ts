import {Component, EventEmitter, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";
import {ClientToSave} from "../../model/ClientToSave";
import {CharacterType} from "../../model/CharacterType";
import {GenderType} from "../../model/GenderType";
import {PhoneNumber} from "../../model/PhoneNumber";


@Component({
  selector: 'main-form-component',
  template: require('./main_form.component.html'),
  styles: [require('./main_form.component.css')]
})

export class MainFormComponent {
  @Output() exit = new EventEmitter<void>();

  userInfo: UserInfo | null = null;
  loadUserInfoButtonEnabled: boolean = true;
  loadUserInfoError: string | null;

  clients: Array<ClientToSave> | null = null;

  constructor(private httpService: HttpService) {}

  loadUserInfoButtonClicked() {
    this.loadUserInfoButtonEnabled = false;
    this.loadUserInfoError = null;

    this.clients = [ClientToSave.copy({
        name: 'Aisultan',
        surname: 'Kali',
        total: 50,
        min: 50,
        max: 50,
        character: CharacterType.CHOLERIC,
        gender: GenderType.MALE,
        factAddress: "Akhan Seri 13a, 32",
        regAddress: "Akhan Seri 13a, 32",
        phoneNumbers: [new PhoneNumber("+77071039297", PhoneType.MOBILE)]
    })];

      this.httpService.get("/auth/userInfo").toPromise().then(result => {
      this.userInfo = result.json() as UserInfo;
      this.userInfo = UserInfo.copy(result.json());

      let phoneType: PhoneType | null = this.userInfo.phoneType;
      console.log(phoneType);
    }, error => {
      console.log(error);
      this.loadUserInfoButtonEnabled = true;
      this.loadUserInfoError = error;
      this.userInfo = null;
    });

    this.httpService.get("/")
  }
}
