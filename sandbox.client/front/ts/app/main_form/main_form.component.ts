import {Component, EventEmitter, OnInit, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";
import {ClientToSave} from "../../model/ClientToSave";
import {GenderType} from "../../model/GenderType";
import {PhoneNumber} from "../../model/PhoneNumber";
import {CharacterType} from "../../model/CharacterType";
import {Charm} from "../../model/Charm";
import {ClientAddress} from "../../model/ClientAddress";
import {AddressType} from "../../model/AddressType";
import {ClientPhone} from "../../model/ClientPhone";

@Component({
  selector: 'main-form-component',
  template: require('./main_form.component.html'),
  styles: [require('./main_form.component.css')]
})

export class MainFormComponent implements OnInit {
  @Output() exit = new EventEmitter<void>();
  onAddClick() {
    window.alert("Hi");
    this.showModal = true;
  }

  showModal: boolean = false;

  userInfo: UserInfo | null = null;
  loadUserInfoButtonEnabled: boolean = true;
  loadUserInfoError: string | null;

  clients: Array<ClientToSave> | null = null;
  selected: ClientToSave | null = null;

  charms: Array<Charm> = null;

  constructor(private httpService: HttpService) {}

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
    // this.httpService.get("/table/get/charms").toPromise().then(result => {
    //   let json: Array<JSON> = result.json();
    //   this.clients = [];
    //   json.forEach(result => {
    //     this.clients.push(ClientToSave.copy(result));
    //   });
    //   console.log(this.clients);
    // }, error => {
    //   console.log(error);
    // });

    this.clients = [ClientToSave.copy({
      name: 'Aisultan',
      surname: 'Kali',
      total: 50,
      min: 50,
      max: 50,
      age: 20,
      character: CharacterType.CHOLERIC,
      gender: GenderType.MALE,
      factAddress: "Akhan Seri 13a, 32",
      regAddress: "Akhan Seri 13a, 32",
      phoneNumbers: [new PhoneNumber("+77071039297", PhoneType.MOBILE)]
    })];

    let clientToSave = ClientToSave.copy({
        id: 4,
        name: 'Anvario',
        surname: 'Bek',
        patronymic: 'Bekovich',
        charm: 2,
        phones: [new ClientPhone(4, '+77777777777', PhoneType.MOBILE)],
        birthDate: 0,
        gender: 'MALE',
        reg: new ClientAddress(4, AddressType.REG, "A", "12", "55"),
        fact: new ClientAddress(4, AddressType.FACT, "A", "12", "55")
    });

    // console.log(clientToSave);

    this.httpService.post("/table/edit", {clientToSave: JSON.stringify(clientToSave)}).toPromise().then(result => {
        this.httpService.post("/table/get", {
            start: 0,
            limit: 10
        }).toPromise().then(result => {
            console.log(result.json());
            this.clients = result.json();
            // console.log(result.json());
        }, error => {
            console.log(error);
        });
      // console.log(result.json());
    }, error => {
      console.log(error);
    });

    // this.httpService.post("/table/remove", {
    //   clientId: 1,
    // }).toPromise().then(result => {
    //   console.log(result.json());
    // }, error => {
    //   console.log(error);
    // });

    // this.httpService.post("/table/get/detail", {
    //   clientId: 1,
    // }).toPromise().then(result => {
    //   console.log(result.json());
    // }, error => {
    //   console.log(error);
    // });
    //
    // this.httpService.get("/table/get/charms").toPromise().then(result => {
    //   console.log(result);
    //   let json:Array<JSON> = result.json();
    //   this.charms = [];
    //   json.forEach(result => {
    //     this.charms.push(Charm.copy(result));
    //   });
    //   console.log(this.charms);
    // }, error => {
    //   console.error(error);
    // });
  }

  onItemClick(client: ClientToSave) {
    console.log(client);
    if (this.selected !== client)
      this.selected = client;
    else this.selected = null;
  }
}
