import {GenderType} from "./GenderType";
import {ClientAddress} from "./ClientAddress";
import {ClientPhone} from "./ClientPhone";
import {Charm} from "./Charm";

export class ClientDetail {
  public id: number /* int */;
  public name: string;
  public surname: string;
  public patronymic: string;
  public gender: GenderType;
  public charm: Charm;
  public birthDate: number /* long */;
  public regAddress: ClientAddress;
  public factAddress: ClientAddress;
  public workPhone: ClientPhone;
  public homePhone: ClientPhone;
  public phones: ClientPhone[];
  public charms: Charm[];

  static copy(json: any) {
    let client: ClientDetail = new ClientDetail();
    client.assign(json);
    return client;
  }

  assign(json: any) {
    this.id = json.id;
    this.name = json.name;
    this.surname = json.surname;
    this.patronymic = json.patronymic;
    this.gender = json.gender;
    this.birthDate = json.birthDate;
    this.regAddress = json.regAddress;
    this.workPhone = json.workPhone;
    this.homePhone = json.homePhone;
    this.factAddress = json.factAddress;
    this.charm = json.charm;
    this.setCharms(json.charms);
    this.phones = [];
    for (let key in json.phones) {
      this.phones.push(ClientPhone.copy(json.phones[key]))
    }
  }

  setCharms(json: any) {
    this.charms = [];
    for (let key in json) {
      if (json.hasOwnProperty(key))
       this.charms.push(Charm.copy(json[key]));
    }
  }
}
