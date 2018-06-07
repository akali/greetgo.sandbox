import {Component, EventEmitter, OnInit, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";
import {ClientRecord} from "../../model/ClientRecord";

@Component({
  selector: 'main-form-component',
  template: require('./main_form.component.html'),
  styles: [require('./main_form.component.css')]
})

export class MainFormComponent implements OnInit {
  @Output() exit = new EventEmitter<void>();
  onAddClick() {
    this.showModal();
  }

  showModal() {

  }

  userInfo: UserInfo | null = null;
  loadUserInfoButtonEnabled: boolean = true;
  loadUserInfoError: string | null;

  clients: Array<ClientRecord> | null = null;
  selected: ClientRecord | null = null;

  constructor(private httpService: HttpService) {}

  onRemove(client: ClientRecord) {
    // console.log("REMOVING:", client);
  }

  onEdit(client: ClientRecord) {
    this.httpService.post("/table/get/detail", {
      clientId: client.id
    }).toPromise().then(result => {
      console.log(result.json());
    }, error => {
      console.log(error);
    });
  }

  loadUserInfoButtonClicked() {
    this.loadUserInfoButtonEnabled = false;
    this.loadUserInfoError = null;

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
  }

  ngOnInit(): void {
    this.httpService.post("/table/get", {
        start: 0,
        limit: 10
    }).toPromise().then(result => {
        console.log(result.json());
        this.clients = result.json();
    }, error => {
        console.log(error);
    });
  }

  onItemClick(client: ClientRecord) {
    console.log(client);
    if (this.selected !== client)
      this.selected = client;
    else this.selected = null;
  }
}
