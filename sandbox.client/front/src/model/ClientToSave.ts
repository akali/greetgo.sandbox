import {GenderType} from "./GenderType";
import {PhoneNumber} from "./PhoneNumber";
import {ClientAddress} from "./ClientAddress";
import {ClientPhone} from "./ClientPhone";

export class ClientToSave {
  public id: number /* int */;
  public name: string;
  public surname: string;
  public patronymic: string;
  public charm: number /* int */;
  public gender: GenderType;
  public regAddress: ClientAddress;
  public factAddress: ClientAddress;
  public birthDate: number /* long */;
  public phones: PhoneNumber[];
  public workPhone: ClientPhone;
  public homePhone: ClientPhone;

  public assign(o: any): ClientToSave {
    this.id = o.id;
    this.name = o.name;
    this.surname = o.surname;
    this.patronymic = o.patronymic;
    this.gender = o.gender;
    this.phones = o.phones;
    this.birthDate = o.birthDate;
    this.regAddress = o.regAddress;
    this.factAddress = o.factAddress;
    this.charm = o.charm;
    this.workPhone = o.workPhone;
    this.homePhone = o.homePhone;
    this.phones = [];
    for (let key in o.phones) {
      this.phones.push(ClientPhone.copy(o.phones[key]))
    }
    return this;
  }

  public static copy(json: any): ClientToSave {
    let result = new ClientToSave();
    result.assign(json);
    return result;
  }
}

