import { PrismaClient } from '@prisma/client';
import bcrypt from 'bcrypt';

const prisma = new PrismaClient();

async function main() {
  // Usuń istniejące dane
  await prisma.review.deleteMany({});
  await prisma.recipeStep.deleteMany({});
  await prisma.recipe.deleteMany({});
  await prisma.ingredient.deleteMany({});
  await prisma.user.deleteMany({});

  console.log('Rozpoczęcie seedowania...');

  // Utwórz użytkowników
  const users = [
    {
      name: 'John',
      surname: 'Doe',
      email: 'john.doe@example.com',
      password: 'password123',
      cookingHours: 0.0,
    },
    {
      name: 'Abby',
      surname: 'Twinkle',
      email: 'abby.twinkle@example.com',
      password: 'password123',
      cookingHours: 0.0,
    },
    {
      name: 'Maria',
      surname: 'Kowalska',
      email: 'maria.kowalska@example.com',
      password: 'password123',
      cookingHours: 15.5,
    },
    {
      name: 'Tomasz',
      surname: 'Nowak',
      email: 'tomasz.nowak@example.com',
      password: 'password123',
      cookingHours: 8.0,
    },
    {
      name: 'Anna',
      surname: 'Wiśniewska',
      email: 'anna.wisniewska@example.com',
      password: 'password123',
      cookingHours: 22.0,
    },
  ];

  for (const user of users) {
    const { name, surname, email, password, cookingHours } = user;
    const hashedPassword = await bcrypt.hash(password, 10);

    await prisma.user.create({
      data: {
        name,
        surname,
        email,
        password: hashedPassword,
        cookingHours,
      },
    });
  }

  console.log(`Dodano ${users.length} użytkowników do bazy danych.`);

  interface Ingredient {
    title: string;
    unit: string;
    category: string;
  }

  // Lista składników często używanych w kuchni
  const ingredients: Ingredient[] = [
    {
      title: 'Mąka pszenna',
      unit: 'g',
      category: 'Produkty zbożowe',
    },
    {
      title: 'Cukier',
      unit: 'g',
      category: 'Słodycze',
    },
    {
      title: 'Sól',
      unit: 'g',
      category: 'Przyprawy',
    },
    {
      title: 'Jajka',
      unit: 'szt',
      category: 'Nabiał',
    },
    {
      title: 'Mleko',
      unit: 'ml',
      category: 'Nabiał',
    },
    {
      title: 'Masło',
      unit: 'g',
      category: 'Nabiał',
    },
    {
      title: 'Olej roślinny',
      unit: 'ml',
      category: 'Tłuszcze',
    },
    {
      title: 'Cebula',
      unit: 'szt',
      category: 'Warzywa',
    },
    {
      title: 'Czosnek',
      unit: 'szt',
      category: 'Warzywa',
    },
    {
      title: 'Pomidory',
      unit: 'g',
      category: 'Warzywa',
    },
    {
      title: 'Kurczak (filet)',
      unit: 'g',
      category: 'Mięso',
    },
    {
      title: 'Ryż',
      unit: 'g',
      category: 'Produkty zbożowe',
    },
    {
      title: 'Makaron',
      unit: 'g',
      category: 'Produkty zbożowe',
    },
    {
      title: 'Ser żółty',
      unit: 'g',
      category: 'Nabiał',
    },
    {
      title: 'Pieprz czarny',
      unit: 'g',
      category: 'Przyprawy',
    },
    {
      title: 'Marchewka',
      unit: 'g',
      category: 'Warzywa',
    },
    {
      title: 'Proszek do pieczenia',
      unit: 'g',
      category: 'Dodatki',
    },
    {
      title: 'Cynamon',
      unit: 'g',
      category: 'Przyprawy',
    },
    {
      title: 'Orzechy włoskie',
      unit: 'g',
      category: 'Orzechy',
    },
    {
      title: 'Drożdże',
      unit: 'g',
      category: 'Dodatki',
    },
    {
      title: 'Woda',
      unit: 'ml',
      category: 'Woda',
    },
    {
      title: 'Oliwa',
      unit: 'ml',
      category: 'Oliwa',
    },
    {
      title: 'Ser biały',
      unit: 'g',
      category: 'Nabiał',
    },
    {
      title: 'Dżem truskawkowy',
      unit: 'g',
      category: 'Słodycze',
    },
    {
      title: 'Mięso mielone',
      unit: 'g',
      category: 'Mięso',
    },
    {
      title: 'Bułka tarta',
      unit: 'g',
      category: 'Produkty zbożowe',
    },
    {
      title: 'Schab',
      unit: 'g',
      category: 'Mięso',
    },
    {
      title: 'Kapusta',
      unit: 'g',
      category: 'Warzywa',
    },
    {
      title: 'Ziemniaki',
      unit: 'g',
      category: 'Warzywa',
    },
    {
      title: 'Jogurt grecki',
      unit: 'g',
      category: 'Nabiał',
    },
    {
      title: 'Ogórek',
      unit: 'szt',
      category: 'Warzywa',
    },
    {
      title: 'Spaghetti',
      unit: 'g',
      category: 'Produkty zbożowe',
    },
    {
      title: 'Bazylia',
      unit: 'g',
      category: 'Przyprawy',
    },
    {
      title: 'Parmezan',
      unit: 'g',
      category: 'Nabiał',
    },
  ];

  // Dodaj każdy składnik do bazy danych
  const createdIngredients: Ingredient[] = [];
  for (const ingredient of ingredients) {
    const created = await prisma.ingredient.create({
      data: ingredient,
    });
    createdIngredients.push(created);
  }

  console.log(`Dodano ${ingredients.length} składników do bazy danych.`);

  interface Step {
    title: string;
    stepType: 'ADD_INGREDIENT' | 'COOKING' | 'DESCRIPTION';
    ingredientId?: number;
    amount?: number;
    time?: string;
    temperature?: number;
    mixSpeed?: number;
    description?: string;
  }

  interface Recipe {
    title: string;
    description: string;
    category: string;
    price: number;
    image: string;
    userId: number;
    steps: Step[];
  }

  // Funkcja pomocnicza do znajdowania ID składnika po nazwie
  function findIngredientIdByTitle(ingredients: Ingredient[], title: string) {
    const found = ingredients.findIndex((ing) => ing.title === title) + 1;
    if (found == -1) {
      throw new Error(`Składnik ${title} nie istnieje`);
    }
    return found;
  }

  // Dodaj przepisy
  const recipes: Recipe[] = [
    {
      title: 'Ciasto marchewkowe',
      description: 'Pyszne i wilgotne ciasto marchewkowe z orzechami włoskimi',
      category: 'deser',
      price: 15.99,
      image:
        'https://res.cloudinary.com/dco9zum8l/image/upload/v1746961475/lthhttzqk7zcpzjvjx0m.png',
      userId: 1,
      steps: [
        {
          title: 'Dodaj mąkę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Mąka pszenna',
          ),
          amount: 250,
        },
        {
          title: 'Dodaj jajka',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Jajka'),
          amount: 3,
        },
        {
          title: 'Dodaj cukier',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Cukier'),
          amount: 200,
        },
        {
          title: 'Dodaj marchewkę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Marchewka',
          ),
          amount: 300,
        },
        {
          title: 'Dodaj proszek do pieczenia',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Proszek do pieczenia',
          ),
          amount: 10,
        },
        {
          title: 'Dodaj cynamon',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Cynamon'),
          amount: 5,
        },
        {
          title: 'Dodaj orzechy włoskie',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Orzechy włoskie',
          ),
          amount: 100,
        },
      ],
    },
    {
      title: 'Naleśniki',
      description: 'Klasyczne naleśniki, idealne na śniadanie lub deser',
      category: 'sniadanie',
      price: 10.5,
      image:
        'https://res.cloudinary.com/dco9zum8l/image/upload/v1746961643/ap4wjpcva3xafifcrjal.png',
      userId: 1,
      steps: [
        {
          title: 'Dodaj mąkę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Mąka pszenna',
          ),
          amount: 200,
        },
        {
          title: 'Dodaj jajka',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Jajka'),
          amount: 2,
        },
        {
          title: 'Dodaj mleko',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Mleko'),
          amount: 500,
        },
        {
          title: 'Dodaj sól',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Sól'),
          amount: 2,
        },
        {
          title: 'Dodaj olej',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Olej roślinny',
          ),
          amount: 30,
        },
      ],
    },
    {
      title: 'Bułki domowe',
      description: 'Puszyste domowe bułki z chrupiącą skórką',
      category: 'sniadanie',
      price: 8.99,
      image:
        'https://res.cloudinary.com/dco9zum8l/image/upload/v1746961550/qvewzvbyos5uexma57mz.png',
      userId: 1,
      steps: [
        {
          title: 'Dodaj mąkę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Mąka pszenna',
          ),
          amount: 500,
        },
        {
          title: 'Dodaj drożdże',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Drożdże'),
          amount: 15,
        },
        {
          title: 'Dodaj sól',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Sól'),
          amount: 10,
        },
      ],
    },
    {
      title: 'Pizza Margherita',
      description: 'Klasyczna włoska pizza z sosem pomidorowym i mozzarellą.',
      category: 'Obiad',
      price: 2.99,
      image:
        'https://res.cloudinary.com/dco9zum8l/image/upload/v1747331152/pizza_c7dikp.jpg',
      userId: 2,
      steps: [
        {
          title: 'Dodaj wodę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Woda'),
          amount: 250,
        },
        {
          title: 'Dodaj drożdże',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Drożdże'),
          amount: 25,
        },
        {
          title: 'Dodaj cukier',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Cukier'),
          amount: 5,
        },
        {
          title: 'Dodaj mąkę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Mąka pszenna',
          ),
          amount: 400,
        },
        {
          title: 'Dodaj oliwę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Oliwa'),
          amount: 20,
        },
        {
          title: 'Dodaj sól',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Sól'),
          amount: 5,
        },
      ],
    },
    {
      title: 'Naleśniki z serem i dżemem',
      description:
        'Delikatne naleśniki z białym serem i słodkim dżemem truskawkowym',
      category: 'deser',
      price: 12.99,
      image:
        'https://res.cloudinary.com/dco9zum8l/image/upload/v1749746606/nalesniki-z-serem-i-dzemem-truskawkowym_pym4q9.jpg',
      userId: 3,
      steps: [
        {
          title: 'Dodaj mąkę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Mąka pszenna',
          ),
          amount: 200,
        },
        {
          title: 'Dodaj jajka',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Jajka'),
          amount: 2,
        },
        {
          title: 'Dodaj mleko',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Mleko'),
          amount: 400,
        },
        {
          title: 'Dodaj ser biały',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Ser biały',
          ),
          amount: 250,
        },
        {
          title: 'Dodaj dżem truskawkowy',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Dżem truskawkowy',
          ),
          amount: 100,
        },
      ],
    },
    {
      title: 'Kotlety mielone',
      description: 'Klasyczne kotlety z mięsa mielonego z bułką tartą i cebulą',
      category: 'obiad',
      price: 18.5,
      image:
        'https://res.cloudinary.com/dco9zum8l/image/upload/v1749746692/mielone_cr54nb.jpg',
      userId: 3,
      steps: [
        {
          title: 'Dodaj mięso mielone',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Mięso mielone',
          ),
          amount: 500,
        },
        {
          title: 'Dodaj cebulę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Cebula'),
          amount: 1,
        },
        {
          title: 'Dodaj jajka',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Jajka'),
          amount: 1,
        },
        {
          title: 'Dodaj bułkę tartą',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Bułka tarta',
          ),
          amount: 50,
        },
        {
          title: 'Dodaj sól',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Sól'),
          amount: 5,
        },
        {
          title: 'Dodaj pieprz',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Pieprz czarny',
          ),
          amount: 2,
        },
      ],
    },
    {
      title: 'Schabowy',
      description: 'Tradycyjny schabowy panierowany z ziemniaczkami i kapustą',
      category: 'obiad',
      price: 22.99,
      image:
        'https://res.cloudinary.com/dco9zum8l/image/upload/v1749746704/schabowy_dpu32z.jpg',
      userId: 4,
      steps: [
        {
          title: 'Dodaj schab',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Schab'),
          amount: 600,
        },
        {
          title: 'Dodaj jajka',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Jajka'),
          amount: 2,
        },
        {
          title: 'Dodaj mąkę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Mąka pszenna',
          ),
          amount: 100,
        },
        {
          title: 'Dodaj bułkę tartą',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Bułka tarta',
          ),
          amount: 100,
        },
        {
          title: 'Dodaj ziemniaki',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Ziemniaki',
          ),
          amount: 800,
        },
        {
          title: 'Dodaj kapustę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Kapusta'),
          amount: 300,
        },
      ],
    },
    {
      title: 'Pierogi',
      description: 'Domowe pierogi z ziemniaczano-serowym farszem',
      category: 'obiad',
      price: 16.99,
      image:
        'https://res.cloudinary.com/dco9zum8l/image/upload/v1749746699/pierogi_bxq5wa.jpg',
      userId: 4,
      steps: [
        {
          title: 'Dodaj mąkę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Mąka pszenna',
          ),
          amount: 400,
        },
        {
          title: 'Dodaj jajka',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Jajka'),
          amount: 1,
        },
        {
          title: 'Dodaj wodę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Woda'),
          amount: 200,
        },
        {
          title: 'Dodaj ziemniaki',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Ziemniaki',
          ),
          amount: 500,
        },
        {
          title: 'Dodaj ser biały',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Ser biały',
          ),
          amount: 200,
        },
        {
          title: 'Dodaj cebulę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Cebula'),
          amount: 1,
        },
      ],
    },
    {
      title: 'Tzatziki',
      description:
        'Grecka przyprawa z jogurtu greckiego z ogórkiem i czosnkiem',
      category: 'przekąska',
      price: 8.99,
      image:
        'https://res.cloudinary.com/dco9zum8l/image/upload/v1749746712/tzatziki_etfpla.jpg',
      userId: 5,
      steps: [
        {
          title: 'Dodaj jogurt grecki',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Jogurt grecki',
          ),
          amount: 400,
        },
        {
          title: 'Dodaj ogórek',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Ogórek'),
          amount: 1,
        },
        {
          title: 'Dodaj czosnek',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Czosnek'),
          amount: 2,
        },
        {
          title: 'Dodaj oliwę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Oliwa'),
          amount: 30,
        },
        {
          title: 'Dodaj sól',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Sól'),
          amount: 3,
        },
      ],
    },
    {
      title: 'Spaghetti',
      description: 'Klasyczne spaghetti z sosem pomidorowym i bazylią',
      category: 'obiad',
      price: 14.5,
      image:
        'https://res.cloudinary.com/dco9zum8l/image/upload/v1749746708/spaghetti_xqmfz7.jpg',
      userId: 5,
      steps: [
        {
          title: 'Dodaj spaghetti',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Spaghetti',
          ),
          amount: 300,
        },
        {
          title: 'Dodaj pomidory',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Pomidory'),
          amount: 400,
        },
        {
          title: 'Dodaj czosnek',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Czosnek'),
          amount: 2,
        },
        {
          title: 'Dodaj oliwę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Oliwa'),
          amount: 50,
        },
        {
          title: 'Dodaj bazylię',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Bazylia'),
          amount: 10,
        },
        {
          title: 'Dodaj parmezan',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Parmezan'),
          amount: 50,
        },
      ],
    },
    // Darmowe przepisy
    {
      title: 'Herbata miętowa',
      description: 'Orzeźwiająca herbata z miętą - idealnie na letnie dni',
      category: 'napój',
      price: 0,
      image:
        'https://res.cloudinary.com/dco9zum8l/image/upload/v1746961643/ap4wjpcva3xafifcrjal.png',
      userId: 1,
      steps: [
        {
          title: 'Dodaj wodę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Woda'),
          amount: 250,
        },
        {
          title: 'Dodaj bazylię (jako mięta)',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Bazylia'),
          amount: 5,
        },
      ],
    },
    {
      title: 'Sałatka z pomidorów',
      description: 'Prosta i zdrowa sałatka z pomidorów z cebulą',
      category: 'przekąska',
      price: 0,
      image:
        'https://res.cloudinary.com/dco9zum8l/image/upload/v1749746712/tzatziki_etfpla.jpg',
      userId: 2,
      steps: [
        {
          title: 'Dodaj pomidory',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Pomidory'),
          amount: 300,
        },
        {
          title: 'Dodaj cebulę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Cebula'),
          amount: 1,
        },
        {
          title: 'Dodaj oliwę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Oliwa'),
          amount: 20,
        },
        {
          title: 'Dodaj sól',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Sól'),
          amount: 3,
        },
      ],
    },
    {
      title: 'Gotowany ryż',
      description: 'Podstawowy przepis na ryż - podstawa wielu dań',
      category: 'dodatek',
      price: 0,
      image:
        'https://res.cloudinary.com/dco9zum8l/image/upload/v1746961550/qvewzvbyos5uexma57mz.png',
      userId: 3,
      steps: [
        {
          title: 'Dodaj ryż',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Ryż'),
          amount: 200,
        },
        {
          title: 'Dodaj wodę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Woda'),
          amount: 400,
        },
        {
          title: 'Dodaj sól',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Sól'),
          amount: 5,
        },
      ],
    },
    {
      title: 'Chleb z masłem',
      description: 'Klasyczne śniadanie - chleb z masłem i szczyptą soli',
      category: 'sniadanie',
      price: 0,
      image:
        'https://res.cloudinary.com/dco9zum8l/image/upload/v1746961550/qvewzvbyos5uexma57mz.png',
      userId: 4,
      steps: [
        {
          title: 'Dodaj mąkę (na chleb)',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Mąka pszenna',
          ),
          amount: 300,
        },
        {
          title: 'Dodaj masło',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Masło'),
          amount: 50,
        },
        {
          title: 'Dodaj sól',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Sól'),
          amount: 2,
        },
      ],
    },
    {
      title: 'Jajecznica podstawowa',
      description: 'Prosta jajecznica na masle - szybkie śniadanie',
      category: 'sniadanie',
      price: 0,
      image:
        'https://res.cloudinary.com/dco9zum8l/image/upload/v1746961643/ap4wjpcva3xafifcrjal.png',
      userId: 5,
      steps: [
        {
          title: 'Dodaj jajka',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Jajka'),
          amount: 3,
        },
        {
          title: 'Dodaj masło',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Masło'),
          amount: 20,
        },
        {
          title: 'Dodaj sól',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Sól'),
          amount: 2,
        },
        {
          title: 'Dodaj pieprz',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Pieprz czarny',
          ),
          amount: 1,
        },
      ],
    },
    {
      title: 'Tost z serem',
      description: 'Szybki tost z serem żółtym - idealne na przekąskę',
      category: 'przekąska',
      price: 0,
      image:
        'https://res.cloudinary.com/dco9zum8l/image/upload/v1749746692/mielone_cr54nb.jpg',
      userId: 1,
      steps: [
        {
          title: 'Dodaj mąkę (na chleb)',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Mąka pszenna',
          ),
          amount: 100,
        },
        {
          title: 'Dodaj ser żółty',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(
            createdIngredients,
            'Ser żółty',
          ),
          amount: 50,
        },
        {
          title: 'Dodaj masło',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Masło'),
          amount: 10,
        },
      ],
    },
    {
      title: 'Makaron z oliwą',
      description: 'Najprostszy makaron z oliwą i czosnkiem',
      category: 'obiad',
      price: 0,
      image:
        'https://res.cloudinary.com/dco9zum8l/image/upload/v1749746708/spaghetti_xqmfz7.jpg',
      userId: 2,
      steps: [
        {
          title: 'Dodaj makaron',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Makaron'),
          amount: 250,
        },
        {
          title: 'Dodaj oliwę',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Oliwa'),
          amount: 30,
        },
        {
          title: 'Dodaj czosnek',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Czosnek'),
          amount: 2,
        },
        {
          title: 'Dodaj sól',
          stepType: 'ADD_INGREDIENT',
          ingredientId: findIngredientIdByTitle(createdIngredients, 'Sól'),
          amount: 5,
        },
      ],
    },
  ];

  // Dodaj przepisy do bazy danych
  for (const recipe of recipes) {
    const { steps, ...recipeData } = recipe;

    const createdRecipe = await prisma.recipe.create({
      data: {
        ...recipeData,
        steps: {
          create: steps,
        },
      },
    });

    console.log(`Dodano przepis: ${recipe.title} (ID: ${createdRecipe.id})`);
  }

  console.log(`Dodano ${recipes.length} przepisów do bazy danych.`);

  // Dodaj reviews dla każdego przepisu
  const reviewsData = [
    // Reviews dla Ciasto marchewkowe (recipeId: 1)
    { userId: 2, recipeId: 1, rating: 5, opinion: '' },
    { userId: 3, recipeId: 1, rating: 4, opinion: '' },
    { userId: 4, recipeId: 1, rating: 5, opinion: '' },

    // Reviews dla Naleśniki (recipeId: 2)
    { userId: 3, recipeId: 2, rating: 4, opinion: '' },
    { userId: 4, recipeId: 2, rating: 3, opinion: '' },
    { userId: 5, recipeId: 2, rating: 5, opinion: '' },

    // Reviews dla Bułki domowe (recipeId: 3)
    { userId: 2, recipeId: 3, rating: 5, opinion: '' },
    { userId: 4, recipeId: 3, rating: 4, opinion: '' },

    // Reviews dla Pizza Margherita (recipeId: 4)
    { userId: 1, recipeId: 4, rating: 5, opinion: '' },
    { userId: 3, recipeId: 4, rating: 4, opinion: '' },
    { userId: 5, recipeId: 4, rating: 5, opinion: '' },

    // Reviews dla Naleśniki z serem i dżemem (recipeId: 5)
    { userId: 1, recipeId: 5, rating: 4, opinion: '' },
    { userId: 2, recipeId: 5, rating: 5, opinion: '' },

    // Reviews dla Kotlety mielone (recipeId: 6)
    { userId: 1, recipeId: 6, rating: 3, opinion: '' },
    { userId: 2, recipeId: 6, rating: 4, opinion: '' },
    { userId: 5, recipeId: 6, rating: 4, opinion: '' },

    // Reviews dla Schabowy (recipeId: 7)
    { userId: 1, recipeId: 7, rating: 5, opinion: '' },
    { userId: 2, recipeId: 7, rating: 5, opinion: '' },

    // Reviews dla Pierogi (recipeId: 8)
    { userId: 1, recipeId: 8, rating: 5, opinion: '' },
    { userId: 3, recipeId: 8, rating: 4, opinion: '' },
    { userId: 5, recipeId: 8, rating: 5, opinion: '' },

    // Reviews dla Tzatziki (recipeId: 9)
    { userId: 1, recipeId: 9, rating: 3, opinion: '' },
    { userId: 2, recipeId: 9, rating: 4, opinion: '' },

    // Reviews dla Spaghetti (recipeId: 10)
    { userId: 1, recipeId: 10, rating: 4, opinion: '' },
    { userId: 3, recipeId: 10, rating: 5, opinion: '' },
    { userId: 4, recipeId: 10, rating: 4, opinion: '' },

    // Reviews dla darmowych przepisów (recipeId: 11-17)
    // Herbata miętowa (recipeId: 11)
    { userId: 2, recipeId: 11, rating: 5, opinion: '' },
    { userId: 3, recipeId: 11, rating: 4, opinion: '' },

    // Sałatka z pomidorów (recipeId: 12)
    { userId: 1, recipeId: 12, rating: 4, opinion: '' },
    { userId: 4, recipeId: 12, rating: 3, opinion: '' },
    { userId: 5, recipeId: 12, rating: 5, opinion: '' },

    // Gotowany ryż (recipeId: 13)
    { userId: 1, recipeId: 13, rating: 5, opinion: '' },
    { userId: 2, recipeId: 13, rating: 4, opinion: '' },

    // Chleb z masłem (recipeId: 14)
    { userId: 2, recipeId: 14, rating: 4, opinion: '' },
    { userId: 3, recipeId: 14, rating: 5, opinion: '' },
    { userId: 5, recipeId: 14, rating: 4, opinion: '' },

    // Jajecznica podstawowa (recipeId: 15)
    { userId: 1, recipeId: 15, rating: 5, opinion: '' },
    { userId: 3, recipeId: 15, rating: 4, opinion: '' },

    // Tost z serem (recipeId: 16)
    { userId: 3, recipeId: 16, rating: 4, opinion: '' },
    { userId: 4, recipeId: 16, rating: 5, opinion: '' },

    // Makaron z oliwą (recipeId: 17)
    { userId: 1, recipeId: 17, rating: 5, opinion: '' },
    { userId: 4, recipeId: 17, rating: 4, opinion: '' },
    { userId: 5, recipeId: 17, rating: 5, opinion: '' },
  ];

  // Dodaj reviews do bazy danych
  for (const review of reviewsData) {
    await prisma.review.create({
      data: review,
    });
  }

  console.log(`Dodano ${reviewsData.length} recenzji do bazy danych.`);
  console.log('Seedowanie zakończone pomyślnie!');
}

// ✅ WYWOŁANIE FUNKCJI MAIN() - poza funkcją!
main()
  .catch((e) => {
    console.error('Błąd podczas seedowania:', e);
    process.exit(1);
  })
  .finally(async () => {
    // Zamknij połączenie Prisma po zakończeniu seedowania
    await prisma.$disconnect();
  });
