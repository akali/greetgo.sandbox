export class Charm {
    public id: number /* int */;
    public name: string;
    public description: string;
    public energy: number /* float */;
    static copy(obj: any): Charm {
        let c: Charm = new Charm();
        c.assign(obj);
        return c;
    }

    assign(obj: any) {
        this.id = obj.id;
        this.name = obj.name;
        this.description = obj.description;
        this.energy = obj.energy;
    }
}
